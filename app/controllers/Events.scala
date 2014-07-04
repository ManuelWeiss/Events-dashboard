package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._

object Events extends Controller {

  /** Central hub for distributing chat messages */
  val (feedOut, feedIn) = Concurrent.broadcast[JsValue]

  /** Controller action serving main page */
  def index = Action { Ok(views.html.index("MF events dashboard")) }

  /** Controller action for POSTing events */
  def postEvent = Action(parse.json) { req => feedIn.push(req.body); Ok }

  /** Controller action for adding events via GET */
  def addEvent(payload: String) = Action { req => feedIn.push(Json.parse(payload)); Ok }

  /** Enumeratee for filtering messages based on stream */
  def filter_by_stream(stream: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "stream").as[String] == stream }

  /** Enumeratee for filtering messages based on stream */
  def aggregate(stream: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "stream").as[String] == stream }

  /** Enumeratee for detecting disconnect of SSE stream */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] =
    Enumeratee.onIterateeDone{ () => println(addr + " - feed disconnected") }

  def aggregate_feed(stream: String) = Action { req =>
    Ok.feed(feedOut
      &> filter_by_stream(stream)
      &> aggregate(stream)
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()
    ).as("text/event-stream")
  }


  /** Controller action serving activity based on stream */
  def raw_feed(stream: String) = Action { req =>
    println(req.remoteAddress + " - feed connected")
    Ok.feed(feedOut
      &> filter_by_stream(stream)
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()
    ).as("text/event-stream")
  }

}

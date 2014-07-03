package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._

object ChatApplication extends Controller {

  /** Central hub for distributing chat messages */
  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  /** Controller action serving main page */
  def index = Action { Ok(views.html.index("MF events dashboard")) }

  /** Controller action for POSTing events */
  def postEvent = Action(parse.json) { req => chatChannel.push(req.body); Ok }

  /** Enumeratee for filtering messages based on stream */
  def filter(stream: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "stream").as[String] == stream }

  /** Enumeratee for detecting disconnect of SSE stream */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] =
    Enumeratee.onIterateeDone{ () => println(addr + " - SSE disconnected") }

  /** Controller action serving activity based on stream */
  def feed(stream: String) = Action { req =>
    println(req.remoteAddress + " - SSE connected")
    Ok.feed(chatOut
      &> filter(stream)
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()
    ).as("text/event-stream")
  }

}

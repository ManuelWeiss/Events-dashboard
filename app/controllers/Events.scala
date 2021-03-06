package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.iteratee.{ Concurrent, Enumeratee }
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
    def addEvent(payload: String) = Action {
        req =>
            feedIn.push(Json.parse(payload))
            val byteArray = List(
                0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00,
                0x01, 0x00, 0x80, 0x00, 0x00, 0xff, 0xff, 0xff,
                0x00, 0x00, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x00,
                0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x44,
                0x01, 0x00, 0x3b).map(_.toByte).toArray
            Ok(byteArray).as("image/gif")
    }

    /** Enumeratee for filtering streams */
    def filter_by_stream(stream: String) = {
        if (stream == "all")
            Enumeratee.filter[JsValue] { _ => true }
        else
            Enumeratee.filter[JsValue] {
                json: JsValue => (json \ "stream").as[String] == stream
            }
    }

    /** Enumeratee for filtering streams */
    def filter_by_kv(key: String, value: String) = {
          Enumeratee.filter[JsValue] {
              json: JsValue => (json \ key).as[String] == value
          }
    }

    /** Enumeratee for aggregating events */
    def aggregate(stream: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "stream").as[String] == stream }

    /** Enumeratee for detecting disconnect of SSE stream */
    def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] =
        Enumeratee.onIterateeDone { () => println(addr + " - feed disconnected") }

    def aggregate_feed(stream: String) = Action { req =>
        Ok.feed(feedOut
            &> filter_by_stream(stream)
            &> aggregate(stream)
            &> Concurrent.buffer(50)
            &> connDeathWatch(req.remoteAddress)
            &> EventSource()).as("text/event-stream")
    }

    /** feed */
    def raw_feed(stream: String) = Action { req =>
        println(req.remoteAddress + " - feed connected")
        Ok.feed(feedOut
            &> filter_by_stream(stream)
            &> Concurrent.buffer(50)
            &> connDeathWatch(req.remoteAddress)
            &> EventSource()).as("text/event-stream")
    }

    def filtered_feed(key: String, value: String) = Action { req =>
        println(req.remoteAddress + " - feed connected")
        Ok.feed(feedOut
            &> filter_by_kv(key, value)
            &> Concurrent.buffer(50)
            &> connDeathWatch(req.remoteAddress)
            &> EventSource()).as("text/event-stream")
    }

}

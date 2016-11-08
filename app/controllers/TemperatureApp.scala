package controllers

import javax.inject.Inject

import models.{Temperature, TemperatureRepo}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class TemperatureApp @Inject()(temperatureRepo: TemperatureRepo)
  extends Controller {
  def showTemperatureNow() = Action.async { implicit rs =>
    temperatureRepo.last
      .map(last => Ok(views.html.temperature(last)))
  }

  def add(temperature: Float) = Action.async { implicit rs =>
    temperatureRepo.add(temperature)
    Future(Redirect(routes.TemperatureApp.showTemperatureNow()))
  }

  private def toJson(temperatures: List[Temperature]): JsArray =
    JsArray(temperatures.map(t =>
      JsObject(Seq("x" -> JsNumber(t.timestamp.getTime),
        "y" -> JsNumber(BigDecimal(t.temperature.toString))))))

  def showTemperaturesOverTime() = Action.async { implicit rs =>
    temperatureRepo.all
      .map(temps => Ok(views.html.timechart(temps)))
  }
}

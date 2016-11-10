package controllers

import javax.inject.Inject

import models.{Temperature, TemperatureRepo}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class TemperatureApp @Inject()(temperatureRepo: TemperatureRepo)
  extends Controller {
  def index() = Action.async { implicit rs =>
    temperatureRepo.all
      .map(temperatures => Ok(views.html.temperature(temperatures.last, temperatures)))
  }

  def add(temperature: Float) = Action.async { implicit rs =>
    temperatureRepo.add(temperature)
    Future(Redirect(routes.TemperatureApp.index()))
  }
}

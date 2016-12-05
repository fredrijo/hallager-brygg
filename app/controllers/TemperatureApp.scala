package controllers

import javax.inject.Inject

import models.TemperatureRepo
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

class TemperatureApp @Inject()(temperatureRepo: TemperatureRepo)
  extends Controller {
  def index() = Action.async { implicit rs =>
    temperatureRepo.all
      .map(temperatures => Ok(views.html.temperature(temperatures.last, temperatures)))
  }

  def add(temperature: Float) = Action { implicit rs =>
    temperatureRepo.add(temperature)
    Ok(views.html.added(temperature))
  }
}

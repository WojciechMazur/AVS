package pl.edu.agh.wmazur.avs.model.entity

trait Identifiable {
  type Id = Int
  def id: Id
}

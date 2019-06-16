package protobuf.pl.edu.agh.wmazur.avs

import com.github.jpbetz.subspace.Vector3
import protobuf.pl.edu.agh.wmazur.avs.model.common.{Vector3 => VectorProto}
import scalapb.TypeMapper

package object model {
  import io.scalaland.chimney.dsl._

  implicit val vector3: TypeMapper[VectorProto, Vector3] =
    TypeMapper[VectorProto, Vector3](_.transformInto[Vector3])(
      _.transformInto[VectorProto])

}

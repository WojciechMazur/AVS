import {Color3, Mesh, Scene, Vector3, PolygonMeshBuilder, Vector2} from "@babylonjs/core"
import {GridMaterial} from "@babylonjs/materials"
import {Entity} from "./Entity"
import { IGeometry, ILane, IIntersection } from "../protobuff/protobuff";

export class Intersection extends Entity{

    constructor(name: string, props: IIntersection, scene: Scene){
        super()
        

		let material = new GridMaterial("material_" + name, scene)
		material.mainColor = Color3.Magenta()

        let shapes = props.geometry!.shapes!.map(shape => {
            let indices = shape.indices!.map(vec3 => {
                return new Vector2(vec3.x!, vec3.z!)
            })
            let polygon =  new PolygonMeshBuilder(name, indices, scene).build(false, 0.05)
            polygon.material = material
            return polygon;
        })


        this.mesh = shapes[0]
        shapes.slice(1).forEach((shape) => {
            this.mesh.addChild(shape)
        })

        this.mesh.position.y = 0.25
        this.name = name


        let entryPointMaterial = new GridMaterial("material_" + name + "_entry_point", scene)
        entryPointMaterial.mainColor = Color3.Green()
        props.entryPoints!.forEach((point, index)=> {
            let newPoint = Mesh.CreateSphere(name + "_entry_" + index, 16, 1, scene)
            newPoint.position = new Vector3(point.x!, 0.5, point.z!)
            newPoint.material = entryPointMaterial
        })

        let exitPointMaterial = new GridMaterial("material_" + name + "_exit_point", scene)
        exitPointMaterial.mainColor = Color3.Red()
        props.exitPoints!.forEach((point, index)=> {
            let newPoint = Mesh.CreateSphere(name + "_exit_" + index, 16, 1, scene)
            newPoint.position = new Vector3(point.x!, 0.5, point.z!)
            newPoint.material = exitPointMaterial
        })
        


        this.name = name
	}
	
}
import {Color3, Mesh, Scene, Vector3, PolygonMeshBuilder, Vector2} from "@babylonjs/core"
import {GridMaterial} from "@babylonjs/materials"
import {Entity} from "./Entity"
import { IVehicleSpec } from "../protobuff/protobuff";

export class Car extends Entity{
	constructor(name: string, position: Vector3, spec: IVehicleSpec, scene: Scene){
		super()
		this.mesh = Mesh.CreateBox(name, 1,  scene)
		this.mesh.position = position
		this.mesh.scaling.set(spec.width!, spec.height!, spec.length!)
		
		let indices = spec!.geometry!.indices!.map(vec3 => {
            return new Vector2(vec3.x!, vec3.z!)
		})

		this.name = name
		let material = new GridMaterial("material_" + name, scene)
		material.mainColor = new Color3(Math.random(), Math.random(),Math.random())
		this.mesh.material = material
	}
	
}
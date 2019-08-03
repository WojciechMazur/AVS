import {Color3, Mesh, Scene, Vector3, PolygonMeshBuilder, Vector2, Quaternion} from "@babylonjs/core"
import {GridMaterial} from "@babylonjs/materials"
import {Entity} from "./Entity"
import { IVehicleSpec, IVehicle } from "../protobuff/protobuff";

export class Car extends Entity{
	private rotationAxis = new Vector3(0, 1, 0)

	public rotate(heading: number) {
		this.mesh.rotationQuaternion =  Quaternion.RotationAxis(this.rotationAxis, heading)
	}
	constructor(name: string, props: IVehicle, scene: Scene){
		super()
		this.mesh = Mesh.CreateBox(name, 1,  scene)
		let position = new Vector3(props.currentPosition!.x!, 0.8, props.currentPosition!.z!)
		this.mesh.position = position
		this.mesh.scaling.set(props.spec!.width!, props.spec!.height!, props.spec!.length!)
		
		this.rotate(props.heading!)
		
		// let indices = spec!.geometry!.shapes![0].indices!.map(vec3 => {
        //     return new Vector2(vec3.x!, vec3.z!)
		// })

		this.name = name
		let material = new GridMaterial("material_" + name, scene)
		material.mainColor = new Color3(Math.random(), Math.random(),Math.random())
		this.mesh.material = material
	}
	
}
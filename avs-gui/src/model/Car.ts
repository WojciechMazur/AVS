import {Color3, Mesh, Scene, Vector3} from "@babylonjs/core"
import {GridMaterial} from "@babylonjs/materials"
import {Entity} from "./Entity"

export class Car extends Entity{
	constructor(name: string, position: Vector3, scene: Scene){
		super()
		this.mesh = Mesh.CreateBox(name, 1,  scene)
		this.mesh.position = position
		this.mesh.scaling.set(2, 1.5, 4.5)
		this.name = name
		let material = new GridMaterial("material_" + name, scene)
		material.mainColor = new Color3(Math.random(), Math.random(),Math.random())
		this.mesh.material = material
	}
	
}
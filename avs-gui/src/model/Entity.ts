import {Mesh, Vector3} from "@babylonjs/core"

export abstract class Entity{
	name!: string
	mesh!: Mesh;
}
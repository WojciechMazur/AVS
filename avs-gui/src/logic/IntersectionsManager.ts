import {Scene, Vector3} from "@babylonjs/core";
import {Car} from "../model/Car"
import {ISystemManager} from "./ISystemManager"

import {WebSocketEntityClient} from "./WebSocketEntityClient"
import { IRoad, IIntersection } from "../protobuff/protobuff";
import { Road } from "../model/Road";
import { Intersection } from "../model/Intersection";

export class IntersectionsManager extends ISystemManager{
    private entities: Map<string, Intersection> = new Map()

    protected webSocketClient: WebSocketEntityClient
	protected scene: Scene
	
	constructor(scene: Scene, websocketClient: WebSocketEntityClient){
		super()
		this.scene = scene
		this.webSocketClient = websocketClient
		this.webSocketClient.setIntersectionsManager(this)
	}
	
	public add(intersections: IIntersection[]){
		intersections.forEach(intersection => {
                let newIntersection = new Intersection("road_"+intersection.id!, intersection!, this.scene)
                console.log("Spawned intersection " + intersection.id)
				this.entities.set(intersection.id!, newIntersection)
			}
		)
	}
	
	public update(intersections: IIntersection[]){
		// roads.forEach(road => {
			// let id = <string>road.id
			// let entity = <Road>this.entities.get(id)
			// let position = road.currentPosition
	// 
			// if (entity != null && position != null) {
				// entity.mesh.lookAt(new Vector3(position.x!, position.y!, position.z!))
				// entity.mesh.position.set(position.x!, position.y!, position.z!)
			// }
	
			// this.entities.set(id, entity)
        // })
	}
	
	public remove(intersectionIds: string[]){
		// vehicleIds.forEach(id => {
		// 	let entity = this.entities.get(id)
		// 	if(entity === undefined){
		// 		console.log("No vehicle with id " + id + ", available ids: " + this.entities.keys())
		// 	}else{
		// 		entity!.mesh.dispose()
		// 	}
		// 	this.entities.delete(id)
        // })
	}
	
	beforeRenderFunctions: { (): void }[] = [
	]
}

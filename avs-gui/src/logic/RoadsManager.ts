import {Scene, Vector3} from "@babylonjs/core";
import {Car} from "../model/Car"
import {ISystemManager} from "./ISystemManager"

import {WebSocketEntityClient} from "./WebSocketEntityClient"
import { IRoad } from "../protobuff/protobuff";
import { Road } from "../model/Road";

export class RoadsManager extends ISystemManager{
    private entities: Map<string, Road> = new Map()

    protected webSocketClient: WebSocketEntityClient
	protected scene: Scene
	
	constructor(scene: Scene, websocketClient: WebSocketEntityClient){
		super()
		this.scene = scene
		this.webSocketClient = websocketClient
		this.webSocketClient.setRoadsManager(this)
	}
	
	public add(roads: IRoad[]){
		roads.forEach(road => {
                let newRoad = new Road("road_"+road.id, road.geometry!, road.lanes!, this.scene)
                console.log("Spawned road " + road.id)
				this.entities.set(road.id!, newRoad)
			}
		)
	}
	
	public update(roads: IRoad[]){
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
	
	public remove(roadIds: string[]){
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

import {Scene, Vector3, Vector2, PolygonMeshBuilder} from "@babylonjs/core";
import {Car} from "../model/Car"
import {IVehicle} from "../protobuff/protobuff"
import {ISystemManager} from "./ISystemManager"
import {WebSocketEntityClient} from "./WebSocketEntityClient"

export class CarsManager extends ISystemManager{
	private entities: Map<string, Car> = new Map([])
	protected webSocketClient: WebSocketEntityClient
	protected scene: Scene
	private vehiclePositionY:number = 0.8
	constructor(scene: Scene, websocketClient: WebSocketEntityClient){
		super()
		this.scene = scene
		this.webSocketClient = websocketClient
		this.webSocketClient.setCarsManager(this)
	}
	
	public add(vehicles: IVehicle[]){
		vehicles.forEach(vehicle => {
			if (vehicle.id != null && vehicle.currentPosition != null) {
				let car = new Car(vehicle.id,
					vehicle, 
					this.scene)
					this.entities.set(vehicle.id, car)
					console.dir("Spawned vehicle with id " + vehicle.id)
			}
		})
	}
	
	public update(vehicles: IVehicle[]){
		vehicles.forEach(vehicle => {
			let id = <string>vehicle.id
			let entity = <Car>this.entities.get(id)
			let position = vehicle.currentPosition
	
			if (entity != null && position != null) {
				// entity.mesh.lookAt(new Vector3(position.x!, this.vehiclePositionY, position.z!))
				entity.mesh.position.set(position.x!, this.vehiclePositionY, position.z!)
				entity.rotate(vehicle.heading! + Math.PI / 2)
			}
	
			this.entities.set(id, entity)
		})
	}
	
	public remove(vehicleIds: string[]){
		vehicleIds.forEach(id => {
			let entity = this.entities.get(id)
			if(entity !== undefined){   
				entity!.mesh.dispose()
			}
			this.entities.delete(id)
		})
	}
	
	beforeRenderFunctions: { (): void }[] = [
	]
}

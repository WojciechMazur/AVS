import {Car} from "../model/Car"
import {SystemManager} from "./LogicManager"

import {WebSocketEntityConnector} from "./WebSocketEntityConnector"

export class CarsManager extends SystemManager{
	private entities: Car[] = []
	private connector: WebSocketEntityConnector
	
	constructor(entities: Car[] = []){
		super()
		this.entities = entities
		this.connector = new WebSocketEntityConnector()
	}
	
	public add(...entities: Car[]){
		this.entities.push(...entities)
	}
	
	private randomMovement = () => {
		this.entities.forEach(entity => {
			entity.mesh.position.addInPlaceFromFloats((Math.random() - 0.5) * 0.1, 0, (Math.random() - 0.5)* 0.1)
		})
	}
	
	beforeRenderFunctions: { (): void }[] = [
		this.randomMovement
	]
}

import {Nullable} from "@babylonjs/core"
import {w3cwebsocket as WebSocket} from "websocket"
import {Envelope, StateUpdate} from "../protobuff/protobuff"
import {CarsManager} from "./CarsManager"
import { RoadsManager } from "./RoadsManager";

const uuidv4 = require('uuid/v4');

export class WebSocketEntityClient{
	private webSocketClient: WebSocket
	private connectionId: string = uuidv4()
	private carsManager: Nullable<CarsManager> = null
	private roadsManager: Nullable<RoadsManager> = null
	
	public setCarsManager(manager: CarsManager){
		this.carsManager = manager
		console.log("Cars manager ready")
	}

	public setRoadsManager(manager: RoadsManager){
		this.roadsManager = manager
		console.log("Roads manager ready")
	}
	
	constructor() {
		this.webSocketClient = new WebSocket("ws://localhost:8081/avs/" + this.connectionId)
		this.webSocketClient.binaryType = 'arraybuffer'
		this.webSocketClient.onerror = this.onError
		this.webSocketClient.onopen = this.onOpen
		this.webSocketClient.onclose = this.onClose
		this.webSocketClient.onmessage = this.onMessage
	}
		private onMessage = (msg: MessageEvent) => {
			let protoMsg = Envelope.decode(new Uint8Array(msg.data))
			if(protoMsg.stateUpdate){
				let update = protoMsg.stateUpdate
				if(update.updateType === StateUpdate.UpdateType.Full){
					console.log("Full update")
				}
				this.roadsManager!.add(update.created!.roads!)
				this.carsManager!.add(update.created!.vehicles!)

				if(update.updateType! == StateUpdate.UpdateType.Delta){
					//Todo roads update / removal
					this.carsManager!.update(update.updated!.vehicles!)
					this.carsManager!.remove(update.deleted!.vehicles!)
				}

			}
	}

		private onError = (error: Error) => {
			console.log('WebSocket connection error: ', error.message)
		}

		private onOpen = () => {
		    console.log("WebSocket Client Connected")
			console.log(this.webSocketClient)
		}
		
		private onClose = () => {
				console.log("WebSocket Closed")
		}
}
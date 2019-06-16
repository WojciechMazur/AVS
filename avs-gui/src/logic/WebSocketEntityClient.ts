import {Nullable} from "@babylonjs/core"
import {w3cwebsocket as WebSocket} from "websocket"
import {Envelope, StateUpdate} from "../protobuff/protobuff"
import {CarsManager} from "./CarsManager"

const uuidv4 = require('uuid/v4');

export class WebSocketEntityClient{
	private webSocketClient: WebSocket
	private connectionId: string = uuidv4()
	private carsManager: Nullable<CarsManager> = null
	
	public setCarsManager(manager: CarsManager){
		this.carsManager = manager
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
			// console.log(protoMsg)
			if(protoMsg.stateUpdate){
				let update = protoMsg.stateUpdate
				// console.log(update)
				if(update.updateType === StateUpdate.UpdateType.Full){
					console.log("Full update")
				}
				this.carsManager!.add(update.created!.vehicles!)
				if(update.updateType! == StateUpdate.UpdateType.Delta){
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

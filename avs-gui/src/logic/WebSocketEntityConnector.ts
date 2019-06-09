import {w3cwebsocket as WebSocket} from "websocket"
const uuidv4 = require('uuid/v4');

export class WebSocketEntityConnector{
	private webSocketClient: WebSocket
	private connectionId: string = uuidv4()
	constructor() {
		this.webSocketClient = new WebSocket("ws://localhost:8081/avs/" + this.connectionId)
		this.webSocketClient.onerror = this.onError
		this.webSocketClient.onopen = this.onOpen
		this.webSocketClient.onclose = this.onClose
		this.webSocketClient.onmessage = this.onMessage
	}
		private onMessage = (msg: MessageEvent) => {
			console.log(msg)
		}

		private onError = (error: Error) => {
				console.log('WebSocket connection error: ', error.message)
		}

		private onOpen = () => {
				console.log("WebSocket Client Connected")
				this.webSocketClient.send("connected")
		}
		
		private onClose = () => {
				console.log("WebSocket Closed")
		}
}

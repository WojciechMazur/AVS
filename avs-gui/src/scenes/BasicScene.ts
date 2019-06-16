import {ArcRotateCamera, Color3, Engine, PointLight, Scene, Vector3} from "@babylonjs/core"
import {IsometricCamera} from "../camera/IsometricCamera"
import {CarsManager} from "../logic/CarsManager"
import {WebSocketEntityClient} from "../logic/WebSocketEntityClient"

export class BasicScene {
	public camera: ArcRotateCamera
	public light: PointLight
	
	private readonly _engine: Engine
	private _scene: Scene
	private carsManager: CarsManager
	private websocketClient: WebSocketEntityClient
	
	constructor(canvas: HTMLCanvasElement, engine: Engine){
		this._engine = engine
		this._scene = new Scene(this._engine)
		
		this.websocketClient = new WebSocketEntityClient()
		this.carsManager	= new CarsManager(this._scene, this.websocketClient)
		this.camera = new IsometricCamera("camera",30, Vector3.Zero(), this._scene)
		this.camera.attachControl(canvas, true, false)
		this._scene.activeCamera=this.camera
		
		
		this.light = new PointLight("pointLight", new Vector3(20,20,20), this._scene)
		this.light.parent = this.camera
		this.light.position = this.camera.position
		this.light.diffuse = new Color3(1,1,1)
		this.light.intensity = 0.4
		
		
	// 	for(let x=0; x<100; x++){
	// 		for(let y=0; y<100; y++){
	// 			let car = new Car("car_" + x + "_" + y, new Vector3(x*5, 0, y*5), this._scene)
	// 			this.carsManager.add(car)
	// 		}
	// 	}
	}
	
	public runRenderLoop(): void {
		let _this = this
		this._engine.runRenderLoop(() => {
			_this.carsManager && _this.carsManager.runLogic()
			
			this._scene.render()
		})
	}
	
}


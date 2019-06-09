import {
	ArcRotateCamera,
	Engine,
	ICameraInput,
	KeyboardEventTypes,
	KeyboardInfo,
	Nullable,
	Observer,
	Scene
} from "@babylonjs/core"

import {Lazy} from "../utils/Lazy";

export class IsometricCameraKeyboardInput implements ICameraInput<ArcRotateCamera> {
	// @ts-ignore
	public camera: ArcRotateCamera
	
	public angularSensibility: number = 5
	public panningSensibility: number = 1
	
	public angularSpeed: number = 0.01
	
	private keysRotateLeft = ['KeyQ']
	private keysRotateRight = ['KeyE']
	
	private keysMoveForward = ['KeyW', 'ArrowUp']
	private keysMoveBackward = ['KeyS', 'ArrowDown']
	private keysMoveLeft = ['KeyA', 'ArrowLeft']
	private keysMoveRight = ['KeyD', 'ArrowRight']
	
	private allKeys: Lazy<string[]> = new Lazy(() => [
			this.keysRotateLeft, this.keysRotateRight,
			this.keysMoveForward, this.keysMoveBackward,
			this.keysMoveLeft, this.keysMoveRight
		].flat()
	)
	

	private _keys = new Array<string>()
	private _onKeyboardObserver: Nullable<Observer<KeyboardInfo>> = null
	private _onCanvasBlurObserver: Nullable<Observer<Engine>> = null
	private _scene: Nullable<Scene> = null

	public checkInputs(): void{
		if(this._onKeyboardObserver){
			this._keys.forEach((keyCode) => {
				const cameraSpeed = this.camera._computeLocalCameraSpeed()
				const speed = cameraSpeed * this.panningSensibility
				// const speed = this.camera.panningInertia
				if(this.keysMoveForward.indexOf(keyCode) !== -1){
					this.camera.inertialPanningY += speed
				} else if(this.keysMoveBackward.indexOf(keyCode) !== -1){
					this.camera.inertialPanningY -= speed
				} else if(this.keysMoveRight.indexOf(keyCode) !== -1){
					this.camera.inertialPanningX += speed
				} else if(this.keysMoveLeft.indexOf(keyCode) !== -1){
					this.camera.inertialPanningX -= speed
				}
			})
			// this._keys = []
		}
	}
	
	
	attachControl(element: HTMLElement, noPreventDefault?: boolean): void {
		this._scene = this.camera.getScene()
		
		this._onCanvasBlurObserver = this._scene.getEngine().onCanvasBlurObservable.add(() => {
			console.log("onCanvasBlur")
			this._keys = []
		})
		
		this._onKeyboardObserver = this._scene.onKeyboardObservable.add((info) => {
				let evt = info.event
				console.log(evt.code)
				if (this.allKeys.value.indexOf(evt.code) !== -1){
					if (info.type === KeyboardEventTypes.KEYDOWN) {
							this._keys.push(evt.code)
					} else {
							let index = this._keys.indexOf(evt.code)
							if (index >= 0) {
								this._keys.splice(index, 1)
							}
					}
					if (evt.preventDefault) {
						if (!noPreventDefault) {
							evt.preventDefault()
						}
					}
				}
			}
		)
	}
	
	detachControl(element: HTMLElement | null): void {
		if (this._scene) {
			if (this._onKeyboardObserver) {
				this._scene.onKeyboardObservable.remove(this._onKeyboardObserver)
			}
			if(this._onCanvasBlurObserver){
				this._scene.getEngine().onCanvasBlurObservable.remove(this._onCanvasBlurObserver)
			}
			this._onKeyboardObserver = null;
			this._onCanvasBlurObserver = null;
		}
		this._keys = []
	}
	
	getClassName(): string {
		return "IsometricCameraKeyboardInput";
	}
	
	getSimpleName(): string {
		return "IsometricInput";
	}
	
}
import {ArcRotateCamera, Scene, Vector3} from "@babylonjs/core"
import {IsometricCameraKeyboardInput} from "./IsometricCameraKeyboardInput"

export class IsometricCamera extends ArcRotateCamera{
	/**
	 * Instantiates a new ArcRotateCamera in a given scene
	 * @param name Defines the name of the camera
	 * @param radius Defines the camera distance from its target
	 * @param target Defines the camera target
	 * @param scene Defines the scene the camera belongs to
	 * @param setActiveOnSceneIfNoneActive Defines wheter the camera should be marked as active if not other active cameras have been defined
	 */
	constructor(name: string, radius: number, target: Vector3, scene: Scene, setActiveOnSceneIfNoneActive?: boolean) {
		super(name, -Math.PI/2, Math.PI / 4, radius, target, scene, setActiveOnSceneIfNoneActive)
		this.inputs.clear()
		this.panningAxis = new Vector3(1,0,1)
		this.upperBetaLimit = Math.PI/3
		this.lowerBetaLimit = 0
		// this.panningInertia = 0.9
		this.lowerRadiusLimit = 5
		this._panningMouseButton = 0
		this.inputs.add(new IsometricCameraKeyboardInput())
		this.inputs.addMouseWheel()
		this.inputs.addPointers()
		this.panningSensibility /= 5
		
	}
}
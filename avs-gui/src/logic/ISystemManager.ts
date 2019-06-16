import {Scene} from "@babylonjs/core"

export abstract class ISystemManager {
	protected abstract beforeRenderFunctions: {(): void}[] = []
	protected abstract scene: Scene
	public runLogic(){
		this.beforeRenderFunctions.forEach(logicFn => logicFn())
	}
	
}
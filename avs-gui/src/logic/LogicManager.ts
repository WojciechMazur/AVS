
export abstract class SystemManager {
	abstract beforeRenderFunctions: {(): void}[] = []
	public runLogic(){
		this.beforeRenderFunctions.forEach(logicFn => logicFn())
	}
	
}
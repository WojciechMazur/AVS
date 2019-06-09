
export class Lazy<T> {
	private _value: T | undefined
	private readonly _initFn: () => T;
	
	constructor(initFn: () => T){
		this._initFn = initFn;
	}
	
	get value() {
		if(!this._value){
			this._value = this._initFn();
		}
		return this._value;
	};
}
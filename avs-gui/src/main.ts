import {Engine} from "@babylonjs/core"
import {BasicScene} from "./scenes/BasicScene"

const canvas = document.getElementById("renderCanvas") as HTMLCanvasElement;
const engine = new Engine(canvas)

let scene = new BasicScene(canvas,engine)
scene.runRenderLoop()

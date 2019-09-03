import {Color3, Mesh, Scene, Vector3, PolygonMeshBuilder, Nullable, Vector2, _BabylonLoaderRegistered, ActionManager, ExecuteCodeAction, ActionEvent} from "@babylonjs/core"
import {GridMaterial} from "@babylonjs/materials"
import {Entity} from "./Entity"
import { IGeometry, ILane, ISpawnPoint, IVector3, ICollectPoint } from "../protobuff/protobuff";

export class Lane extends Entity{
    constructor(name: string,
          geometry: IGeometry,
          spawnPoint: ISpawnPoint | null | undefined | Nullable<ISpawnPoint>,
          collectPoint: ICollectPoint | null | undefined | Nullable<ICollectPoint>,
          entryPoint: IVector3, 
          exitPoint: IVector3,
           scene: Scene){
        super()

        let material = new GridMaterial("material_" + name, scene)
        material.mainColor = Color3.Gray()
        
        let shapes = geometry.shapes!.map(shape => {
            let indices = shape.indices!.map(vec3 => {
                return new Vector2(vec3.x!, vec3.z!)
            })
            let polygon =  new PolygonMeshBuilder(name, indices, scene).build(false, 0.1)
            polygon.material = material
            return polygon;
        })


        this.mesh = shapes[0]
        shapes.slice(1).forEach((shape) => {
            this.mesh.addChild(shape)
        })

        this.mesh.actionManager = new ActionManager(scene)
        let wsc
        let onHover = (meshEvent: ActionEvent) => {
            var but = document.createElement("span");
            but.setAttribute("id", "hoverText_" + name);
            var sty = but.style;
            sty.position = "absolute";
            sty.lineHeight = "5em";
            sty.paddingLeft = "10px";
            sty.paddingRight = "10px";
            sty.color = "#ffff00";
            sty.border = "5pt ridge blue";
            sty.borderRadius = "12px";
            sty.backgroundColor = "none";
            sty.fontSize = "48pt";
            sty.top = scene.pointerY + "px";
            sty.left = screen.width + scene.pointerX + "px";
            sty.cursor = "pointer";
            but.setAttribute("onclick", "alert('ouch!')");
            document.body.appendChild(but);
    
            but.textContent = name;
        }

        let onOut = (meshEven: ActionEvent) => {
            while (document.getElementById("hoverText_" + name)) {
                document.getElementById("hoverText_" + name)!.parentNode!.removeChild(document.getElementById("hoverText_" + name)!);
            }
        }

        this.mesh.actionManager.registerAction(
            new ExecuteCodeAction(
                ActionManager.OnPointerOverTrigger,
                onHover
            )
        );
        this.mesh.actionManager.registerAction(
            new ExecuteCodeAction(
                ActionManager.OnPointerOutTrigger,
                onOut
            )
        );
    

        this.name = name
        this.mesh.position.y = 0.2        

        //let entryPointMesh = Mesh.CreateSphere(name + "_entry_point", 16, 2, scene)
        //entryPointMesh.position = new Vector3(entryPoint.x!, 1, entryPoint.z!)
        //let spawPointIndicatiorMaterial = new GridMaterial("material_" + name + "_entrypoint_indicatior", scene)
        //spawPointIndicatiorMaterial.mainColor = Color3.Green()
        //entryPointMesh.material = spawPointIndicatiorMaterial

        //let exitPointMesh = Mesh.CreateSphere(name + "_exit_point", 16, 2, scene)
        ///exitPointMesh.position = new Vector3(exitPoint.x!, 1, exitPoint.z!)
        //let exitPointIndicatiorMaterial = new GridMaterial("material_" + name + "_exitpoint_indicatior", scene)
        //exitPointIndicatiorMaterial.mainColor = Color3.Red()
        //exitPointMesh.material = exitPointIndicatiorMaterial
        
        //this.mesh
        //.addChild(entryPointMesh)
        //.addChild(exitPointMesh)
        
        if(spawnPoint){
            let spawnPointIndices = spawnPoint!.geometry!.shapes![0].indices!.map(vec3 => {
                return new Vector2(vec3.x!, vec3.z!)
             })
            let spawnAreaMesh = new PolygonMeshBuilder(name + "_spawn_area", spawnPointIndices, scene).build(false,0.1)
            spawnAreaMesh.position.y = 0.6
            let spawnAreaMaterial = new GridMaterial("material_" + name + "_spawn_area", scene)
            spawnAreaMaterial.mainColor = Color3.Blue()
            spawnAreaMesh.material = spawnAreaMaterial
            console.log("Spawned spawnPoint area in lane " + name)
            this.mesh.addChild(spawnAreaMesh)
        }

        if(collectPoint){
            let collectPointIndices = collectPoint!.geometry!.shapes![0].indices!.map(vec3 => {
                return new Vector2(vec3.x!, vec3.z!)
             })
            let collectAreaMesh = new PolygonMeshBuilder(name + "_collect_area", collectPointIndices, scene).build(false,0.1)
            collectAreaMesh.position.y = 0.6
            let collectAreaMaterial = new GridMaterial("material_" + name + "_collect_area", scene)
            collectAreaMaterial.mainColor = Color3.Red()
            collectAreaMesh.material = collectAreaMaterial
            console.log("Created collect area in lane " + name)
            this.mesh.addChild(collectAreaMesh)
        }


        this.name = name
	}
	
}
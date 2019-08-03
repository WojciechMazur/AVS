import {Color3, Mesh, Scene, Vector3, PolygonMeshBuilder, Vector2} from "@babylonjs/core"
import {GridMaterial} from "@babylonjs/materials"
import {Entity} from "./Entity"
import { IGeometry, ILane } from "../protobuff/protobuff";
import { Lane } from "./Lane";
var earcut = require('earcut')

export class Road extends Entity{
    lanes: Lane[] = []

    constructor(name: string, geometry: IGeometry, lanes: ILane[], scene: Scene){
        super()

        let material = new GridMaterial("material_" + name, scene)
        material.mainColor = Color3.Black()
        
        let shapes = geometry.shapes!.map(shape => {
            let indices = shape.indices!.map(vec3 => {
                return new Vector2(vec3.x!, vec3.z!)
            })
            let polygon =  new PolygonMeshBuilder(name, indices, scene).build(false, 0.05)
            polygon.material = material
            return polygon;
        })

        this.mesh = shapes[0]
        shapes.slice(1).forEach((shape) => {
            this.mesh.addChild(shape)
        })

        this.name = name

        this.lanes = lanes.map(lane =>{
            let laneName = name + "_lane_" + lane.id
            const newLane =  new Lane(laneName, lane.geometry!, lane.spawnPoint, lane.collectPoint, lane.entryPoint!, lane.exitPoint!, scene)
            this.mesh.addChild(newLane.mesh)
            return newLane
        }
        )
	}
	
}
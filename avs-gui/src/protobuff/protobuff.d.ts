import * as $protobuf from "protobufjs";
/** Properties of a Vector3. */
export interface IVector3 {

    /** Vector3 x */
    x?: (number|null);

    /** Vector3 y */
    y?: (number|null);

    /** Vector3 z */
    z?: (number|null);
}

/** Represents a Vector3. */
export class Vector3 implements IVector3 {

    /**
     * Constructs a new Vector3.
     * @param [properties] Properties to set
     */
    constructor(properties?: IVector3);

    /** Vector3 x. */
    public x: number;

    /** Vector3 y. */
    public y: number;

    /** Vector3 z. */
    public z: number;

    /**
     * Creates a new Vector3 instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Vector3 instance
     */
    public static create(properties?: IVector3): Vector3;

    /**
     * Encodes the specified Vector3 message. Does not implicitly {@link Vector3.verify|verify} messages.
     * @param message Vector3 message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IVector3, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Vector3 message, length delimited. Does not implicitly {@link Vector3.verify|verify} messages.
     * @param message Vector3 message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IVector3, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a Vector3 message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Vector3
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Vector3;

    /**
     * Decodes a Vector3 message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Vector3
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Vector3;

    /**
     * Verifies a Vector3 message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a Vector3 message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Vector3
     */
    public static fromObject(object: { [k: string]: any }): Vector3;

    /**
     * Creates a plain object from a Vector3 message. Also converts values to other types if specified.
     * @param message Vector3
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Vector3, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Vector3 to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a BoundingBox. */
export interface IBoundingBox {

    /** BoundingBox minX */
    minX?: (number|null);

    /** BoundingBox maxX */
    maxX?: (number|null);

    /** BoundingBox minY */
    minY?: (number|null);

    /** BoundingBox maxY */
    maxY?: (number|null);
}

/** Represents a BoundingBox. */
export class BoundingBox implements IBoundingBox {

    /**
     * Constructs a new BoundingBox.
     * @param [properties] Properties to set
     */
    constructor(properties?: IBoundingBox);

    /** BoundingBox minX. */
    public minX: number;

    /** BoundingBox maxX. */
    public maxX: number;

    /** BoundingBox minY. */
    public minY: number;

    /** BoundingBox maxY. */
    public maxY: number;

    /**
     * Creates a new BoundingBox instance using the specified properties.
     * @param [properties] Properties to set
     * @returns BoundingBox instance
     */
    public static create(properties?: IBoundingBox): BoundingBox;

    /**
     * Encodes the specified BoundingBox message. Does not implicitly {@link BoundingBox.verify|verify} messages.
     * @param message BoundingBox message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IBoundingBox, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified BoundingBox message, length delimited. Does not implicitly {@link BoundingBox.verify|verify} messages.
     * @param message BoundingBox message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IBoundingBox, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a BoundingBox message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns BoundingBox
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): BoundingBox;

    /**
     * Decodes a BoundingBox message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns BoundingBox
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): BoundingBox;

    /**
     * Verifies a BoundingBox message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a BoundingBox message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns BoundingBox
     */
    public static fromObject(object: { [k: string]: any }): BoundingBox;

    /**
     * Creates a plain object from a BoundingBox message. Also converts values to other types if specified.
     * @param message BoundingBox
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: BoundingBox, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this BoundingBox to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a Geometry. */
export interface IGeometry {

    /** Geometry position */
    position?: (IVector3|null);

    /** Geometry indices */
    indices?: (IVector3[]|null);
}

/** Represents a Geometry. */
export class Geometry implements IGeometry {

    /**
     * Constructs a new Geometry.
     * @param [properties] Properties to set
     */
    constructor(properties?: IGeometry);

    /** Geometry position. */
    public position?: (IVector3|null);

    /** Geometry indices. */
    public indices: IVector3[];

    /**
     * Creates a new Geometry instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Geometry instance
     */
    public static create(properties?: IGeometry): Geometry;

    /**
     * Encodes the specified Geometry message. Does not implicitly {@link Geometry.verify|verify} messages.
     * @param message Geometry message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IGeometry, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Geometry message, length delimited. Does not implicitly {@link Geometry.verify|verify} messages.
     * @param message Geometry message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IGeometry, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a Geometry message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Geometry
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Geometry;

    /**
     * Decodes a Geometry message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Geometry
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Geometry;

    /**
     * Verifies a Geometry message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a Geometry message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Geometry
     */
    public static fromObject(object: { [k: string]: any }): Geometry;

    /**
     * Creates a plain object from a Geometry message. Also converts values to other types if specified.
     * @param message Geometry
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Geometry, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Geometry to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of an Envelope. */
export interface IEnvelope {

    /** Envelope acknowledged */
    acknowledged?: (Envelope.IAcknowledged|null);

    /** Envelope simulationEvent */
    simulationEvent?: (IStateModificationEvent|null);

    /** Envelope connectivityEvents */
    connectivityEvents?: (IConnectivityEvents|null);

    /** Envelope stateUpdate */
    stateUpdate?: (IStateUpdate|null);

    /** Envelope stateRequest */
    stateRequest?: (IStateRequest|null);
}

/** Represents an Envelope. */
export class Envelope implements IEnvelope {

    /**
     * Constructs a new Envelope.
     * @param [properties] Properties to set
     */
    constructor(properties?: IEnvelope);

    /** Envelope acknowledged. */
    public acknowledged?: (Envelope.IAcknowledged|null);

    /** Envelope simulationEvent. */
    public simulationEvent?: (IStateModificationEvent|null);

    /** Envelope connectivityEvents. */
    public connectivityEvents?: (IConnectivityEvents|null);

    /** Envelope stateUpdate. */
    public stateUpdate?: (IStateUpdate|null);

    /** Envelope stateRequest. */
    public stateRequest?: (IStateRequest|null);

    /** Envelope message. */
    public message?: ("acknowledged"|"simulationEvent"|"connectivityEvents"|"stateUpdate"|"stateRequest");

    /**
     * Creates a new Envelope instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Envelope instance
     */
    public static create(properties?: IEnvelope): Envelope;

    /**
     * Encodes the specified Envelope message. Does not implicitly {@link Envelope.verify|verify} messages.
     * @param message Envelope message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IEnvelope, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Envelope message, length delimited. Does not implicitly {@link Envelope.verify|verify} messages.
     * @param message Envelope message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IEnvelope, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes an Envelope message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Envelope
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Envelope;

    /**
     * Decodes an Envelope message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Envelope
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Envelope;

    /**
     * Verifies an Envelope message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates an Envelope message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Envelope
     */
    public static fromObject(object: { [k: string]: any }): Envelope;

    /**
     * Creates a plain object from an Envelope message. Also converts values to other types if specified.
     * @param message Envelope
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Envelope, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Envelope to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

export namespace Envelope {

    /** Properties of an Acknowledged. */
    interface IAcknowledged {
    }

    /** Represents an Acknowledged. */
    class Acknowledged implements IAcknowledged {

        /**
         * Constructs a new Acknowledged.
         * @param [properties] Properties to set
         */
        constructor(properties?: Envelope.IAcknowledged);

        /**
         * Creates a new Acknowledged instance using the specified properties.
         * @param [properties] Properties to set
         * @returns Acknowledged instance
         */
        public static create(properties?: Envelope.IAcknowledged): Envelope.Acknowledged;

        /**
         * Encodes the specified Acknowledged message. Does not implicitly {@link Envelope.Acknowledged.verify|verify} messages.
         * @param message Acknowledged message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: Envelope.IAcknowledged, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Encodes the specified Acknowledged message, length delimited. Does not implicitly {@link Envelope.Acknowledged.verify|verify} messages.
         * @param message Acknowledged message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encodeDelimited(message: Envelope.IAcknowledged, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes an Acknowledged message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns Acknowledged
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Envelope.Acknowledged;

        /**
         * Decodes an Acknowledged message from the specified reader or buffer, length delimited.
         * @param reader Reader or buffer to decode from
         * @returns Acknowledged
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Envelope.Acknowledged;

        /**
         * Verifies an Acknowledged message.
         * @param message Plain object to verify
         * @returns `null` if valid, otherwise the reason why it is not
         */
        public static verify(message: { [k: string]: any }): (string|null);

        /**
         * Creates an Acknowledged message from a plain object. Also converts values to their respective internal types.
         * @param object Plain object
         * @returns Acknowledged
         */
        public static fromObject(object: { [k: string]: any }): Envelope.Acknowledged;

        /**
         * Creates a plain object from an Acknowledged message. Also converts values to other types if specified.
         * @param message Acknowledged
         * @param [options] Conversion options
         * @returns Plain object
         */
        public static toObject(message: Envelope.Acknowledged, options?: $protobuf.IConversionOptions): { [k: string]: any };

        /**
         * Converts this Acknowledged to JSON.
         * @returns JSON object
         */
        public toJSON(): { [k: string]: any };
    }
}

/** Properties of a ConnectivityEvents. */
export interface IConnectivityEvents {

    /** ConnectivityEvents type */
    type?: (ConnectivityEvents.EventType|null);

    /** ConnectivityEvents targetId */
    targetId?: (string|null);

    /** ConnectivityEvents activeClients */
    activeClients?: (number|null);
}

/** Represents a ConnectivityEvents. */
export class ConnectivityEvents implements IConnectivityEvents {

    /**
     * Constructs a new ConnectivityEvents.
     * @param [properties] Properties to set
     */
    constructor(properties?: IConnectivityEvents);

    /** ConnectivityEvents type. */
    public type: ConnectivityEvents.EventType;

    /** ConnectivityEvents targetId. */
    public targetId: string;

    /** ConnectivityEvents activeClients. */
    public activeClients: number;

    /**
     * Creates a new ConnectivityEvents instance using the specified properties.
     * @param [properties] Properties to set
     * @returns ConnectivityEvents instance
     */
    public static create(properties?: IConnectivityEvents): ConnectivityEvents;

    /**
     * Encodes the specified ConnectivityEvents message. Does not implicitly {@link ConnectivityEvents.verify|verify} messages.
     * @param message ConnectivityEvents message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IConnectivityEvents, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified ConnectivityEvents message, length delimited. Does not implicitly {@link ConnectivityEvents.verify|verify} messages.
     * @param message ConnectivityEvents message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IConnectivityEvents, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a ConnectivityEvents message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns ConnectivityEvents
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): ConnectivityEvents;

    /**
     * Decodes a ConnectivityEvents message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns ConnectivityEvents
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): ConnectivityEvents;

    /**
     * Verifies a ConnectivityEvents message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a ConnectivityEvents message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns ConnectivityEvents
     */
    public static fromObject(object: { [k: string]: any }): ConnectivityEvents;

    /**
     * Creates a plain object from a ConnectivityEvents message. Also converts values to other types if specified.
     * @param message ConnectivityEvents
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: ConnectivityEvents, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this ConnectivityEvents to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

export namespace ConnectivityEvents {

    /** EventType enum. */
    enum EventType {
        ClientJoined = 0,
        ClientLeaved = 1
    }
}

/** Properties of a StateModificationEvent. */
export interface IStateModificationEvent {

    /** StateModificationEvent create */
    create?: (ICreateEntity|null);

    /** StateModificationEvent delete */
    "delete"?: (IDeleteEntity|null);

    /** StateModificationEvent edit */
    edit?: (IEditEntity|null);
}

/** Represents a StateModificationEvent. */
export class StateModificationEvent implements IStateModificationEvent {

    /**
     * Constructs a new StateModificationEvent.
     * @param [properties] Properties to set
     */
    constructor(properties?: IStateModificationEvent);

    /** StateModificationEvent create. */
    public create?: (ICreateEntity|null);

    /** StateModificationEvent delete. */
    public delete?: (IDeleteEntity|null);

    /** StateModificationEvent edit. */
    public edit?: (IEditEntity|null);

    /** StateModificationEvent command. */
    public command?: ("create"|"delete"|"edit");

    /**
     * Creates a new StateModificationEvent instance using the specified properties.
     * @param [properties] Properties to set
     * @returns StateModificationEvent instance
     */
    public static create(properties?: IStateModificationEvent): StateModificationEvent;

    /**
     * Encodes the specified StateModificationEvent message. Does not implicitly {@link StateModificationEvent.verify|verify} messages.
     * @param message StateModificationEvent message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IStateModificationEvent, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified StateModificationEvent message, length delimited. Does not implicitly {@link StateModificationEvent.verify|verify} messages.
     * @param message StateModificationEvent message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IStateModificationEvent, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a StateModificationEvent message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns StateModificationEvent
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): StateModificationEvent;

    /**
     * Decodes a StateModificationEvent message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns StateModificationEvent
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): StateModificationEvent;

    /**
     * Verifies a StateModificationEvent message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a StateModificationEvent message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns StateModificationEvent
     */
    public static fromObject(object: { [k: string]: any }): StateModificationEvent;

    /**
     * Creates a plain object from a StateModificationEvent message. Also converts values to other types if specified.
     * @param message StateModificationEvent
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: StateModificationEvent, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this StateModificationEvent to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a StateRequest. */
export interface IStateRequest {

    /** StateRequest type */
    type?: (StateUpdate.UpdateType|null);
}

/** Represents a StateRequest. */
export class StateRequest implements IStateRequest {

    /**
     * Constructs a new StateRequest.
     * @param [properties] Properties to set
     */
    constructor(properties?: IStateRequest);

    /** StateRequest type. */
    public type: StateUpdate.UpdateType;

    /**
     * Creates a new StateRequest instance using the specified properties.
     * @param [properties] Properties to set
     * @returns StateRequest instance
     */
    public static create(properties?: IStateRequest): StateRequest;

    /**
     * Encodes the specified StateRequest message. Does not implicitly {@link StateRequest.verify|verify} messages.
     * @param message StateRequest message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IStateRequest, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified StateRequest message, length delimited. Does not implicitly {@link StateRequest.verify|verify} messages.
     * @param message StateRequest message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IStateRequest, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a StateRequest message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns StateRequest
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): StateRequest;

    /**
     * Decodes a StateRequest message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns StateRequest
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): StateRequest;

    /**
     * Verifies a StateRequest message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a StateRequest message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns StateRequest
     */
    public static fromObject(object: { [k: string]: any }): StateRequest;

    /**
     * Creates a plain object from a StateRequest message. Also converts values to other types if specified.
     * @param message StateRequest
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: StateRequest, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this StateRequest to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a CreateEntity. */
export interface ICreateEntity {
}

/** Represents a CreateEntity. */
export class CreateEntity implements ICreateEntity {

    /**
     * Constructs a new CreateEntity.
     * @param [properties] Properties to set
     */
    constructor(properties?: ICreateEntity);

    /**
     * Creates a new CreateEntity instance using the specified properties.
     * @param [properties] Properties to set
     * @returns CreateEntity instance
     */
    public static create(properties?: ICreateEntity): CreateEntity;

    /**
     * Encodes the specified CreateEntity message. Does not implicitly {@link CreateEntity.verify|verify} messages.
     * @param message CreateEntity message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: ICreateEntity, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified CreateEntity message, length delimited. Does not implicitly {@link CreateEntity.verify|verify} messages.
     * @param message CreateEntity message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: ICreateEntity, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a CreateEntity message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns CreateEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): CreateEntity;

    /**
     * Decodes a CreateEntity message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns CreateEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): CreateEntity;

    /**
     * Verifies a CreateEntity message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a CreateEntity message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns CreateEntity
     */
    public static fromObject(object: { [k: string]: any }): CreateEntity;

    /**
     * Creates a plain object from a CreateEntity message. Also converts values to other types if specified.
     * @param message CreateEntity
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: CreateEntity, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this CreateEntity to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a DeleteEntity. */
export interface IDeleteEntity {
}

/** Represents a DeleteEntity. */
export class DeleteEntity implements IDeleteEntity {

    /**
     * Constructs a new DeleteEntity.
     * @param [properties] Properties to set
     */
    constructor(properties?: IDeleteEntity);

    /**
     * Creates a new DeleteEntity instance using the specified properties.
     * @param [properties] Properties to set
     * @returns DeleteEntity instance
     */
    public static create(properties?: IDeleteEntity): DeleteEntity;

    /**
     * Encodes the specified DeleteEntity message. Does not implicitly {@link DeleteEntity.verify|verify} messages.
     * @param message DeleteEntity message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IDeleteEntity, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified DeleteEntity message, length delimited. Does not implicitly {@link DeleteEntity.verify|verify} messages.
     * @param message DeleteEntity message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IDeleteEntity, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a DeleteEntity message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns DeleteEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): DeleteEntity;

    /**
     * Decodes a DeleteEntity message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns DeleteEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): DeleteEntity;

    /**
     * Verifies a DeleteEntity message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a DeleteEntity message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns DeleteEntity
     */
    public static fromObject(object: { [k: string]: any }): DeleteEntity;

    /**
     * Creates a plain object from a DeleteEntity message. Also converts values to other types if specified.
     * @param message DeleteEntity
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: DeleteEntity, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this DeleteEntity to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of an EditEntity. */
export interface IEditEntity {
}

/** Represents an EditEntity. */
export class EditEntity implements IEditEntity {

    /**
     * Constructs a new EditEntity.
     * @param [properties] Properties to set
     */
    constructor(properties?: IEditEntity);

    /**
     * Creates a new EditEntity instance using the specified properties.
     * @param [properties] Properties to set
     * @returns EditEntity instance
     */
    public static create(properties?: IEditEntity): EditEntity;

    /**
     * Encodes the specified EditEntity message. Does not implicitly {@link EditEntity.verify|verify} messages.
     * @param message EditEntity message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IEditEntity, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified EditEntity message, length delimited. Does not implicitly {@link EditEntity.verify|verify} messages.
     * @param message EditEntity message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IEditEntity, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes an EditEntity message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns EditEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): EditEntity;

    /**
     * Decodes an EditEntity message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns EditEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): EditEntity;

    /**
     * Verifies an EditEntity message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates an EditEntity message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns EditEntity
     */
    public static fromObject(object: { [k: string]: any }): EditEntity;

    /**
     * Creates a plain object from an EditEntity message. Also converts values to other types if specified.
     * @param message EditEntity
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: EditEntity, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this EditEntity to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a StateUpdate. */
export interface IStateUpdate {

    /** StateUpdate updateType */
    updateType?: (StateUpdate.UpdateType|null);

    /** StateUpdate timestamp */
    timestamp?: (number|Long|null);

    /** StateUpdate created */
    created?: (StateUpdate.ICreated|null);

    /** StateUpdate updated */
    updated?: (StateUpdate.IUpdated|null);

    /** StateUpdate deleted */
    deleted?: (StateUpdate.IDeleted|null);

    /** StateUpdate meta */
    meta?: (StateUpdate.IUpdateMeta|null);
}

/** Represents a StateUpdate. */
export class StateUpdate implements IStateUpdate {

    /**
     * Constructs a new StateUpdate.
     * @param [properties] Properties to set
     */
    constructor(properties?: IStateUpdate);

    /** StateUpdate updateType. */
    public updateType: StateUpdate.UpdateType;

    /** StateUpdate timestamp. */
    public timestamp: (number|Long);

    /** StateUpdate created. */
    public created?: (StateUpdate.ICreated|null);

    /** StateUpdate updated. */
    public updated?: (StateUpdate.IUpdated|null);

    /** StateUpdate deleted. */
    public deleted?: (StateUpdate.IDeleted|null);

    /** StateUpdate meta. */
    public meta?: (StateUpdate.IUpdateMeta|null);

    /**
     * Creates a new StateUpdate instance using the specified properties.
     * @param [properties] Properties to set
     * @returns StateUpdate instance
     */
    public static create(properties?: IStateUpdate): StateUpdate;

    /**
     * Encodes the specified StateUpdate message. Does not implicitly {@link StateUpdate.verify|verify} messages.
     * @param message StateUpdate message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IStateUpdate, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified StateUpdate message, length delimited. Does not implicitly {@link StateUpdate.verify|verify} messages.
     * @param message StateUpdate message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IStateUpdate, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a StateUpdate message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns StateUpdate
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): StateUpdate;

    /**
     * Decodes a StateUpdate message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns StateUpdate
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): StateUpdate;

    /**
     * Verifies a StateUpdate message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a StateUpdate message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns StateUpdate
     */
    public static fromObject(object: { [k: string]: any }): StateUpdate;

    /**
     * Creates a plain object from a StateUpdate message. Also converts values to other types if specified.
     * @param message StateUpdate
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: StateUpdate, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this StateUpdate to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

export namespace StateUpdate {

    /** Properties of a Created. */
    interface ICreated {

        /** Created vehicles */
        vehicles?: (IVehicle[]|null);

        /** Created roads */
        roads?: (IRoad[]|null);

        /** Created intersections */
        intersections?: (IIntersection[]|null);
    }

    /** Represents a Created. */
    class Created implements ICreated {

        /**
         * Constructs a new Created.
         * @param [properties] Properties to set
         */
        constructor(properties?: StateUpdate.ICreated);

        /** Created vehicles. */
        public vehicles: IVehicle[];

        /** Created roads. */
        public roads: IRoad[];

        /** Created intersections. */
        public intersections: IIntersection[];

        /**
         * Creates a new Created instance using the specified properties.
         * @param [properties] Properties to set
         * @returns Created instance
         */
        public static create(properties?: StateUpdate.ICreated): StateUpdate.Created;

        /**
         * Encodes the specified Created message. Does not implicitly {@link StateUpdate.Created.verify|verify} messages.
         * @param message Created message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: StateUpdate.ICreated, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Encodes the specified Created message, length delimited. Does not implicitly {@link StateUpdate.Created.verify|verify} messages.
         * @param message Created message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encodeDelimited(message: StateUpdate.ICreated, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a Created message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns Created
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): StateUpdate.Created;

        /**
         * Decodes a Created message from the specified reader or buffer, length delimited.
         * @param reader Reader or buffer to decode from
         * @returns Created
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): StateUpdate.Created;

        /**
         * Verifies a Created message.
         * @param message Plain object to verify
         * @returns `null` if valid, otherwise the reason why it is not
         */
        public static verify(message: { [k: string]: any }): (string|null);

        /**
         * Creates a Created message from a plain object. Also converts values to their respective internal types.
         * @param object Plain object
         * @returns Created
         */
        public static fromObject(object: { [k: string]: any }): StateUpdate.Created;

        /**
         * Creates a plain object from a Created message. Also converts values to other types if specified.
         * @param message Created
         * @param [options] Conversion options
         * @returns Plain object
         */
        public static toObject(message: StateUpdate.Created, options?: $protobuf.IConversionOptions): { [k: string]: any };

        /**
         * Converts this Created to JSON.
         * @returns JSON object
         */
        public toJSON(): { [k: string]: any };
    }

    /** Properties of an Updated. */
    interface IUpdated {

        /** Updated vehicles */
        vehicles?: (IVehicle[]|null);

        /** Updated roads */
        roads?: (IRoad[]|null);

        /** Updated intersections */
        intersections?: (IIntersection[]|null);
    }

    /** Represents an Updated. */
    class Updated implements IUpdated {

        /**
         * Constructs a new Updated.
         * @param [properties] Properties to set
         */
        constructor(properties?: StateUpdate.IUpdated);

        /** Updated vehicles. */
        public vehicles: IVehicle[];

        /** Updated roads. */
        public roads: IRoad[];

        /** Updated intersections. */
        public intersections: IIntersection[];

        /**
         * Creates a new Updated instance using the specified properties.
         * @param [properties] Properties to set
         * @returns Updated instance
         */
        public static create(properties?: StateUpdate.IUpdated): StateUpdate.Updated;

        /**
         * Encodes the specified Updated message. Does not implicitly {@link StateUpdate.Updated.verify|verify} messages.
         * @param message Updated message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: StateUpdate.IUpdated, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Encodes the specified Updated message, length delimited. Does not implicitly {@link StateUpdate.Updated.verify|verify} messages.
         * @param message Updated message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encodeDelimited(message: StateUpdate.IUpdated, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes an Updated message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns Updated
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): StateUpdate.Updated;

        /**
         * Decodes an Updated message from the specified reader or buffer, length delimited.
         * @param reader Reader or buffer to decode from
         * @returns Updated
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): StateUpdate.Updated;

        /**
         * Verifies an Updated message.
         * @param message Plain object to verify
         * @returns `null` if valid, otherwise the reason why it is not
         */
        public static verify(message: { [k: string]: any }): (string|null);

        /**
         * Creates an Updated message from a plain object. Also converts values to their respective internal types.
         * @param object Plain object
         * @returns Updated
         */
        public static fromObject(object: { [k: string]: any }): StateUpdate.Updated;

        /**
         * Creates a plain object from an Updated message. Also converts values to other types if specified.
         * @param message Updated
         * @param [options] Conversion options
         * @returns Plain object
         */
        public static toObject(message: StateUpdate.Updated, options?: $protobuf.IConversionOptions): { [k: string]: any };

        /**
         * Converts this Updated to JSON.
         * @returns JSON object
         */
        public toJSON(): { [k: string]: any };
    }

    /** Properties of a Deleted. */
    interface IDeleted {

        /** Deleted vehicles */
        vehicles?: (string[]|null);

        /** Deleted roads */
        roads?: (string[]|null);

        /** Deleted intersections */
        intersections?: (string[]|null);
    }

    /** Represents a Deleted. */
    class Deleted implements IDeleted {

        /**
         * Constructs a new Deleted.
         * @param [properties] Properties to set
         */
        constructor(properties?: StateUpdate.IDeleted);

        /** Deleted vehicles. */
        public vehicles: string[];

        /** Deleted roads. */
        public roads: string[];

        /** Deleted intersections. */
        public intersections: string[];

        /**
         * Creates a new Deleted instance using the specified properties.
         * @param [properties] Properties to set
         * @returns Deleted instance
         */
        public static create(properties?: StateUpdate.IDeleted): StateUpdate.Deleted;

        /**
         * Encodes the specified Deleted message. Does not implicitly {@link StateUpdate.Deleted.verify|verify} messages.
         * @param message Deleted message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: StateUpdate.IDeleted, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Encodes the specified Deleted message, length delimited. Does not implicitly {@link StateUpdate.Deleted.verify|verify} messages.
         * @param message Deleted message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encodeDelimited(message: StateUpdate.IDeleted, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes a Deleted message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns Deleted
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): StateUpdate.Deleted;

        /**
         * Decodes a Deleted message from the specified reader or buffer, length delimited.
         * @param reader Reader or buffer to decode from
         * @returns Deleted
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): StateUpdate.Deleted;

        /**
         * Verifies a Deleted message.
         * @param message Plain object to verify
         * @returns `null` if valid, otherwise the reason why it is not
         */
        public static verify(message: { [k: string]: any }): (string|null);

        /**
         * Creates a Deleted message from a plain object. Also converts values to their respective internal types.
         * @param object Plain object
         * @returns Deleted
         */
        public static fromObject(object: { [k: string]: any }): StateUpdate.Deleted;

        /**
         * Creates a plain object from a Deleted message. Also converts values to other types if specified.
         * @param message Deleted
         * @param [options] Conversion options
         * @returns Plain object
         */
        public static toObject(message: StateUpdate.Deleted, options?: $protobuf.IConversionOptions): { [k: string]: any };

        /**
         * Converts this Deleted to JSON.
         * @returns JSON object
         */
        public toJSON(): { [k: string]: any };
    }

    /** UpdateType enum. */
    enum UpdateType {
        Delta = 0,
        Full = 1
    }

    /** Properties of an UpdateMeta. */
    interface IUpdateMeta {

        /** UpdateMeta updatesPerSecond */
        updatesPerSecond?: (number|null);
    }

    /** Represents an UpdateMeta. */
    class UpdateMeta implements IUpdateMeta {

        /**
         * Constructs a new UpdateMeta.
         * @param [properties] Properties to set
         */
        constructor(properties?: StateUpdate.IUpdateMeta);

        /** UpdateMeta updatesPerSecond. */
        public updatesPerSecond: number;

        /**
         * Creates a new UpdateMeta instance using the specified properties.
         * @param [properties] Properties to set
         * @returns UpdateMeta instance
         */
        public static create(properties?: StateUpdate.IUpdateMeta): StateUpdate.UpdateMeta;

        /**
         * Encodes the specified UpdateMeta message. Does not implicitly {@link StateUpdate.UpdateMeta.verify|verify} messages.
         * @param message UpdateMeta message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encode(message: StateUpdate.IUpdateMeta, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Encodes the specified UpdateMeta message, length delimited. Does not implicitly {@link StateUpdate.UpdateMeta.verify|verify} messages.
         * @param message UpdateMeta message or plain object to encode
         * @param [writer] Writer to encode to
         * @returns Writer
         */
        public static encodeDelimited(message: StateUpdate.IUpdateMeta, writer?: $protobuf.Writer): $protobuf.Writer;

        /**
         * Decodes an UpdateMeta message from the specified reader or buffer.
         * @param reader Reader or buffer to decode from
         * @param [length] Message length if known beforehand
         * @returns UpdateMeta
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): StateUpdate.UpdateMeta;

        /**
         * Decodes an UpdateMeta message from the specified reader or buffer, length delimited.
         * @param reader Reader or buffer to decode from
         * @returns UpdateMeta
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): StateUpdate.UpdateMeta;

        /**
         * Verifies an UpdateMeta message.
         * @param message Plain object to verify
         * @returns `null` if valid, otherwise the reason why it is not
         */
        public static verify(message: { [k: string]: any }): (string|null);

        /**
         * Creates an UpdateMeta message from a plain object. Also converts values to their respective internal types.
         * @param object Plain object
         * @returns UpdateMeta
         */
        public static fromObject(object: { [k: string]: any }): StateUpdate.UpdateMeta;

        /**
         * Creates a plain object from an UpdateMeta message. Also converts values to other types if specified.
         * @param message UpdateMeta
         * @param [options] Conversion options
         * @returns Plain object
         */
        public static toObject(message: StateUpdate.UpdateMeta, options?: $protobuf.IConversionOptions): { [k: string]: any };

        /**
         * Converts this UpdateMeta to JSON.
         * @returns JSON object
         */
        public toJSON(): { [k: string]: any };
    }
}

/** Properties of a Vehicle. */
export interface IVehicle {

    /** Vehicle id */
    id?: (string|null);

    /** Vehicle currentPosition */
    currentPosition?: (IVector3|null);

    /** Vehicle targetPosition */
    targetPosition?: (IVector3|null);

    /** Vehicle acceleration */
    acceleration?: (number|null);

    /** Vehicle speed */
    speed?: (number|null);

    /** Vehicle spec */
    spec?: (IVehicleSpec|null);
}

/** Represents a Vehicle. */
export class Vehicle implements IVehicle {

    /**
     * Constructs a new Vehicle.
     * @param [properties] Properties to set
     */
    constructor(properties?: IVehicle);

    /** Vehicle id. */
    public id: string;

    /** Vehicle currentPosition. */
    public currentPosition?: (IVector3|null);

    /** Vehicle targetPosition. */
    public targetPosition?: (IVector3|null);

    /** Vehicle acceleration. */
    public acceleration: number;

    /** Vehicle speed. */
    public speed: number;

    /** Vehicle spec. */
    public spec?: (IVehicleSpec|null);

    /**
     * Creates a new Vehicle instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Vehicle instance
     */
    public static create(properties?: IVehicle): Vehicle;

    /**
     * Encodes the specified Vehicle message. Does not implicitly {@link Vehicle.verify|verify} messages.
     * @param message Vehicle message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IVehicle, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Vehicle message, length delimited. Does not implicitly {@link Vehicle.verify|verify} messages.
     * @param message Vehicle message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IVehicle, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a Vehicle message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Vehicle
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Vehicle;

    /**
     * Decodes a Vehicle message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Vehicle
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Vehicle;

    /**
     * Verifies a Vehicle message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a Vehicle message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Vehicle
     */
    public static fromObject(object: { [k: string]: any }): Vehicle;

    /**
     * Creates a plain object from a Vehicle message. Also converts values to other types if specified.
     * @param message Vehicle
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Vehicle, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Vehicle to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a VehicleSpec. */
export interface IVehicleSpec {

    /** VehicleSpec width */
    width?: (number|null);

    /** VehicleSpec length */
    length?: (number|null);

    /** VehicleSpec height */
    height?: (number|null);

    /** VehicleSpec geometry */
    geometry?: (IGeometry|null);
}

/** Represents a VehicleSpec. */
export class VehicleSpec implements IVehicleSpec {

    /**
     * Constructs a new VehicleSpec.
     * @param [properties] Properties to set
     */
    constructor(properties?: IVehicleSpec);

    /** VehicleSpec width. */
    public width: number;

    /** VehicleSpec length. */
    public length: number;

    /** VehicleSpec height. */
    public height: number;

    /** VehicleSpec geometry. */
    public geometry?: (IGeometry|null);

    /**
     * Creates a new VehicleSpec instance using the specified properties.
     * @param [properties] Properties to set
     * @returns VehicleSpec instance
     */
    public static create(properties?: IVehicleSpec): VehicleSpec;

    /**
     * Encodes the specified VehicleSpec message. Does not implicitly {@link VehicleSpec.verify|verify} messages.
     * @param message VehicleSpec message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IVehicleSpec, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified VehicleSpec message, length delimited. Does not implicitly {@link VehicleSpec.verify|verify} messages.
     * @param message VehicleSpec message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IVehicleSpec, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a VehicleSpec message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns VehicleSpec
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): VehicleSpec;

    /**
     * Decodes a VehicleSpec message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns VehicleSpec
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): VehicleSpec;

    /**
     * Verifies a VehicleSpec message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a VehicleSpec message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns VehicleSpec
     */
    public static fromObject(object: { [k: string]: any }): VehicleSpec;

    /**
     * Creates a plain object from a VehicleSpec message. Also converts values to other types if specified.
     * @param message VehicleSpec
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: VehicleSpec, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this VehicleSpec to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a Road. */
export interface IRoad {

    /** Road id */
    id?: (string|null);

    /** Road lanes */
    lanes?: (ILane[]|null);

    /** Road geometry */
    geometry?: (IGeometry|null);
}

/** Represents a Road. */
export class Road implements IRoad {

    /**
     * Constructs a new Road.
     * @param [properties] Properties to set
     */
    constructor(properties?: IRoad);

    /** Road id. */
    public id: string;

    /** Road lanes. */
    public lanes: ILane[];

    /** Road geometry. */
    public geometry?: (IGeometry|null);

    /**
     * Creates a new Road instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Road instance
     */
    public static create(properties?: IRoad): Road;

    /**
     * Encodes the specified Road message. Does not implicitly {@link Road.verify|verify} messages.
     * @param message Road message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IRoad, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Road message, length delimited. Does not implicitly {@link Road.verify|verify} messages.
     * @param message Road message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IRoad, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a Road message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Road
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Road;

    /**
     * Decodes a Road message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Road
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Road;

    /**
     * Verifies a Road message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a Road message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Road
     */
    public static fromObject(object: { [k: string]: any }): Road;

    /**
     * Creates a plain object from a Road message. Also converts values to other types if specified.
     * @param message Road
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Road, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Road to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a Lane. */
export interface ILane {

    /** Lane id */
    id?: (string|null);

    /** Lane geometry */
    geometry?: (IGeometry|null);

    /** Lane entryPoint */
    entryPoint?: (IVector3|null);

    /** Lane exitPoint */
    exitPoint?: (IVector3|null);

    /** Lane spawnPoint */
    spawnPoint?: (ISpawnPoint|null);

    /** Lane collectPoint */
    collectPoint?: (ICollectPoint|null);
}

/** Represents a Lane. */
export class Lane implements ILane {

    /**
     * Constructs a new Lane.
     * @param [properties] Properties to set
     */
    constructor(properties?: ILane);

    /** Lane id. */
    public id: string;

    /** Lane geometry. */
    public geometry?: (IGeometry|null);

    /** Lane entryPoint. */
    public entryPoint?: (IVector3|null);

    /** Lane exitPoint. */
    public exitPoint?: (IVector3|null);

    /** Lane spawnPoint. */
    public spawnPoint?: (ISpawnPoint|null);

    /** Lane collectPoint. */
    public collectPoint?: (ICollectPoint|null);

    /**
     * Creates a new Lane instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Lane instance
     */
    public static create(properties?: ILane): Lane;

    /**
     * Encodes the specified Lane message. Does not implicitly {@link Lane.verify|verify} messages.
     * @param message Lane message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: ILane, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Lane message, length delimited. Does not implicitly {@link Lane.verify|verify} messages.
     * @param message Lane message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: ILane, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a Lane message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Lane
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Lane;

    /**
     * Decodes a Lane message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Lane
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Lane;

    /**
     * Verifies a Lane message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a Lane message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Lane
     */
    public static fromObject(object: { [k: string]: any }): Lane;

    /**
     * Creates a plain object from a Lane message. Also converts values to other types if specified.
     * @param message Lane
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Lane, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Lane to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a SpawnPoint. */
export interface ISpawnPoint {

    /** SpawnPoint geometry */
    geometry?: (IGeometry|null);
}

/** Represents a SpawnPoint. */
export class SpawnPoint implements ISpawnPoint {

    /**
     * Constructs a new SpawnPoint.
     * @param [properties] Properties to set
     */
    constructor(properties?: ISpawnPoint);

    /** SpawnPoint geometry. */
    public geometry?: (IGeometry|null);

    /**
     * Creates a new SpawnPoint instance using the specified properties.
     * @param [properties] Properties to set
     * @returns SpawnPoint instance
     */
    public static create(properties?: ISpawnPoint): SpawnPoint;

    /**
     * Encodes the specified SpawnPoint message. Does not implicitly {@link SpawnPoint.verify|verify} messages.
     * @param message SpawnPoint message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: ISpawnPoint, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified SpawnPoint message, length delimited. Does not implicitly {@link SpawnPoint.verify|verify} messages.
     * @param message SpawnPoint message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: ISpawnPoint, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a SpawnPoint message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns SpawnPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): SpawnPoint;

    /**
     * Decodes a SpawnPoint message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns SpawnPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): SpawnPoint;

    /**
     * Verifies a SpawnPoint message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a SpawnPoint message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns SpawnPoint
     */
    public static fromObject(object: { [k: string]: any }): SpawnPoint;

    /**
     * Creates a plain object from a SpawnPoint message. Also converts values to other types if specified.
     * @param message SpawnPoint
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: SpawnPoint, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this SpawnPoint to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of a CollectPoint. */
export interface ICollectPoint {

    /** CollectPoint geometry */
    geometry?: (IGeometry|null);
}

/** Represents a CollectPoint. */
export class CollectPoint implements ICollectPoint {

    /**
     * Constructs a new CollectPoint.
     * @param [properties] Properties to set
     */
    constructor(properties?: ICollectPoint);

    /** CollectPoint geometry. */
    public geometry?: (IGeometry|null);

    /**
     * Creates a new CollectPoint instance using the specified properties.
     * @param [properties] Properties to set
     * @returns CollectPoint instance
     */
    public static create(properties?: ICollectPoint): CollectPoint;

    /**
     * Encodes the specified CollectPoint message. Does not implicitly {@link CollectPoint.verify|verify} messages.
     * @param message CollectPoint message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: ICollectPoint, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified CollectPoint message, length delimited. Does not implicitly {@link CollectPoint.verify|verify} messages.
     * @param message CollectPoint message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: ICollectPoint, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a CollectPoint message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns CollectPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): CollectPoint;

    /**
     * Decodes a CollectPoint message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns CollectPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): CollectPoint;

    /**
     * Verifies a CollectPoint message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a CollectPoint message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns CollectPoint
     */
    public static fromObject(object: { [k: string]: any }): CollectPoint;

    /**
     * Creates a plain object from a CollectPoint message. Also converts values to other types if specified.
     * @param message CollectPoint
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: CollectPoint, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this CollectPoint to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

/** Properties of an Intersection. */
export interface IIntersection {
}

/** Represents an Intersection. */
export class Intersection implements IIntersection {

    /**
     * Constructs a new Intersection.
     * @param [properties] Properties to set
     */
    constructor(properties?: IIntersection);

    /**
     * Creates a new Intersection instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Intersection instance
     */
    public static create(properties?: IIntersection): Intersection;

    /**
     * Encodes the specified Intersection message. Does not implicitly {@link Intersection.verify|verify} messages.
     * @param message Intersection message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IIntersection, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Intersection message, length delimited. Does not implicitly {@link Intersection.verify|verify} messages.
     * @param message Intersection message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IIntersection, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes an Intersection message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Intersection
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Intersection;

    /**
     * Decodes an Intersection message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Intersection
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Intersection;

    /**
     * Verifies an Intersection message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates an Intersection message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Intersection
     */
    public static fromObject(object: { [k: string]: any }): Intersection;

    /**
     * Creates a plain object from an Intersection message. Also converts values to other types if specified.
     * @param message Intersection
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Intersection, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Intersection to JSON.
     * @returns JSON object
     */
    public toJSON(): { [k: string]: any };
}

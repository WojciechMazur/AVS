import * as $protobuf from "protobufjs";
/** Properties of a Position. */
export interface IPosition {

    /** Position x */
    x?: (number|null);

    /** Position y */
    y?: (number|null);

    /** Position z */
    z?: (number|null);
}

/** Represents a Position. */
export class Position implements IPosition {

    /**
     * Constructs a new Position.
     * @param [properties] Properties to set
     */
    constructor(properties?: IPosition);

    /** Position x. */
    public x: number;

    /** Position y. */
    public y: number;

    /** Position z. */
    public z: number;

    /**
     * Creates a new Position instance using the specified properties.
     * @param [properties] Properties to set
     * @returns Position instance
     */
    public static create(properties?: IPosition): Position;

    /**
     * Encodes the specified Position message. Does not implicitly {@link Position.verify|verify} messages.
     * @param message Position message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encode(message: IPosition, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Encodes the specified Position message, length delimited. Does not implicitly {@link Position.verify|verify} messages.
     * @param message Position message or plain object to encode
     * @param [writer] Writer to encode to
     * @returns Writer
     */
    public static encodeDelimited(message: IPosition, writer?: $protobuf.Writer): $protobuf.Writer;

    /**
     * Decodes a Position message from the specified reader or buffer.
     * @param reader Reader or buffer to decode from
     * @param [length] Message length if known beforehand
     * @returns Position
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): Position;

    /**
     * Decodes a Position message from the specified reader or buffer, length delimited.
     * @param reader Reader or buffer to decode from
     * @returns Position
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): Position;

    /**
     * Verifies a Position message.
     * @param message Plain object to verify
     * @returns `null` if valid, otherwise the reason why it is not
     */
    public static verify(message: { [k: string]: any }): (string|null);

    /**
     * Creates a Position message from a plain object. Also converts values to their respective internal types.
     * @param object Plain object
     * @returns Position
     */
    public static fromObject(object: { [k: string]: any }): Position;

    /**
     * Creates a plain object from a Position message. Also converts values to other types if specified.
     * @param message Position
     * @param [options] Conversion options
     * @returns Plain object
     */
    public static toObject(message: Position, options?: $protobuf.IConversionOptions): { [k: string]: any };

    /**
     * Converts this Position to JSON.
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
    currentPosition?: (IPosition|null);

    /** Vehicle targetPosition */
    targetPosition?: (IPosition|null);

    /** Vehicle acceleration */
    acceleration?: (number|null);

    /** Vehicle speed */
    speed?: (number|null);
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
    public currentPosition?: (IPosition|null);

    /** Vehicle targetPosition. */
    public targetPosition?: (IPosition|null);

    /** Vehicle acceleration. */
    public acceleration: number;

    /** Vehicle speed. */
    public speed: number;

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

/** Properties of a Road. */
export interface IRoad {
}

/** Represents a Road. */
export class Road implements IRoad {

    /**
     * Constructs a new Road.
     * @param [properties] Properties to set
     */
    constructor(properties?: IRoad);

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
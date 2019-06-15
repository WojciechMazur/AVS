/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
import * as $protobuf from "protobufjs/minimal";

// Common aliases
const $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

// Exported root namespace
const $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

export const Position = $root.Position = (() => {

    /**
     * Properties of a Position.
     * @exports IPosition
     * @interface IPosition
     * @property {number|null} [x] Position x
     * @property {number|null} [y] Position y
     * @property {number|null} [z] Position z
     */

    /**
     * Constructs a new Position.
     * @exports Position
     * @classdesc Represents a Position.
     * @implements IPosition
     * @constructor
     * @param {IPosition=} [properties] Properties to set
     */
    function Position(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Position x.
     * @member {number} x
     * @memberof Position
     * @instance
     */
    Position.prototype.x = 0;

    /**
     * Position y.
     * @member {number} y
     * @memberof Position
     * @instance
     */
    Position.prototype.y = 0;

    /**
     * Position z.
     * @member {number} z
     * @memberof Position
     * @instance
     */
    Position.prototype.z = 0;

    /**
     * Creates a new Position instance using the specified properties.
     * @function create
     * @memberof Position
     * @static
     * @param {IPosition=} [properties] Properties to set
     * @returns {Position} Position instance
     */
    Position.create = function create(properties) {
        return new Position(properties);
    };

    /**
     * Encodes the specified Position message. Does not implicitly {@link Position.verify|verify} messages.
     * @function encode
     * @memberof Position
     * @static
     * @param {IPosition} message Position message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Position.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.x != null && message.hasOwnProperty("x"))
            writer.uint32(/* id 1, wireType 5 =*/13).float(message.x);
        if (message.y != null && message.hasOwnProperty("y"))
            writer.uint32(/* id 2, wireType 5 =*/21).float(message.y);
        if (message.z != null && message.hasOwnProperty("z"))
            writer.uint32(/* id 3, wireType 5 =*/29).float(message.z);
        return writer;
    };

    /**
     * Encodes the specified Position message, length delimited. Does not implicitly {@link Position.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Position
     * @static
     * @param {IPosition} message Position message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Position.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Position message from the specified reader or buffer.
     * @function decode
     * @memberof Position
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Position} Position
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Position.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Position();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.x = reader.float();
                break;
            case 2:
                message.y = reader.float();
                break;
            case 3:
                message.z = reader.float();
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a Position message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Position
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Position} Position
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Position.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Position message.
     * @function verify
     * @memberof Position
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Position.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.x != null && message.hasOwnProperty("x"))
            if (typeof message.x !== "number")
                return "x: number expected";
        if (message.y != null && message.hasOwnProperty("y"))
            if (typeof message.y !== "number")
                return "y: number expected";
        if (message.z != null && message.hasOwnProperty("z"))
            if (typeof message.z !== "number")
                return "z: number expected";
        return null;
    };

    /**
     * Creates a Position message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Position
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Position} Position
     */
    Position.fromObject = function fromObject(object) {
        if (object instanceof $root.Position)
            return object;
        let message = new $root.Position();
        if (object.x != null)
            message.x = Number(object.x);
        if (object.y != null)
            message.y = Number(object.y);
        if (object.z != null)
            message.z = Number(object.z);
        return message;
    };

    /**
     * Creates a plain object from a Position message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Position
     * @static
     * @param {Position} message Position
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Position.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults) {
            object.x = 0;
            object.y = 0;
            object.z = 0;
        }
        if (message.x != null && message.hasOwnProperty("x"))
            object.x = options.json && !isFinite(message.x) ? String(message.x) : message.x;
        if (message.y != null && message.hasOwnProperty("y"))
            object.y = options.json && !isFinite(message.y) ? String(message.y) : message.y;
        if (message.z != null && message.hasOwnProperty("z"))
            object.z = options.json && !isFinite(message.z) ? String(message.z) : message.z;
        return object;
    };

    /**
     * Converts this Position to JSON.
     * @function toJSON
     * @memberof Position
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Position.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Position;
})();

export const Envelope = $root.Envelope = (() => {

    /**
     * Properties of an Envelope.
     * @exports IEnvelope
     * @interface IEnvelope
     * @property {Envelope.IAcknowledged|null} [acknowledged] Envelope acknowledged
     * @property {IStateModificationEvent|null} [simulationEvent] Envelope simulationEvent
     * @property {IConnectivityEvents|null} [connectivityEvents] Envelope connectivityEvents
     * @property {IStateUpdate|null} [stateUpdate] Envelope stateUpdate
     * @property {IStateRequest|null} [stateRequest] Envelope stateRequest
     */

    /**
     * Constructs a new Envelope.
     * @exports Envelope
     * @classdesc Represents an Envelope.
     * @implements IEnvelope
     * @constructor
     * @param {IEnvelope=} [properties] Properties to set
     */
    function Envelope(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Envelope acknowledged.
     * @member {Envelope.IAcknowledged|null|undefined} acknowledged
     * @memberof Envelope
     * @instance
     */
    Envelope.prototype.acknowledged = null;

    /**
     * Envelope simulationEvent.
     * @member {IStateModificationEvent|null|undefined} simulationEvent
     * @memberof Envelope
     * @instance
     */
    Envelope.prototype.simulationEvent = null;

    /**
     * Envelope connectivityEvents.
     * @member {IConnectivityEvents|null|undefined} connectivityEvents
     * @memberof Envelope
     * @instance
     */
    Envelope.prototype.connectivityEvents = null;

    /**
     * Envelope stateUpdate.
     * @member {IStateUpdate|null|undefined} stateUpdate
     * @memberof Envelope
     * @instance
     */
    Envelope.prototype.stateUpdate = null;

    /**
     * Envelope stateRequest.
     * @member {IStateRequest|null|undefined} stateRequest
     * @memberof Envelope
     * @instance
     */
    Envelope.prototype.stateRequest = null;

    // OneOf field names bound to virtual getters and setters
    let $oneOfFields;

    /**
     * Envelope message.
     * @member {"acknowledged"|"simulationEvent"|"connectivityEvents"|"stateUpdate"|"stateRequest"|undefined} message
     * @memberof Envelope
     * @instance
     */
    Object.defineProperty(Envelope.prototype, "message", {
        get: $util.oneOfGetter($oneOfFields = ["acknowledged", "simulationEvent", "connectivityEvents", "stateUpdate", "stateRequest"]),
        set: $util.oneOfSetter($oneOfFields)
    });

    /**
     * Creates a new Envelope instance using the specified properties.
     * @function create
     * @memberof Envelope
     * @static
     * @param {IEnvelope=} [properties] Properties to set
     * @returns {Envelope} Envelope instance
     */
    Envelope.create = function create(properties) {
        return new Envelope(properties);
    };

    /**
     * Encodes the specified Envelope message. Does not implicitly {@link Envelope.verify|verify} messages.
     * @function encode
     * @memberof Envelope
     * @static
     * @param {IEnvelope} message Envelope message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Envelope.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.acknowledged != null && message.hasOwnProperty("acknowledged"))
            $root.Envelope.Acknowledged.encode(message.acknowledged, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
        if (message.simulationEvent != null && message.hasOwnProperty("simulationEvent"))
            $root.StateModificationEvent.encode(message.simulationEvent, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        if (message.connectivityEvents != null && message.hasOwnProperty("connectivityEvents"))
            $root.ConnectivityEvents.encode(message.connectivityEvents, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
        if (message.stateUpdate != null && message.hasOwnProperty("stateUpdate"))
            $root.StateUpdate.encode(message.stateUpdate, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
        if (message.stateRequest != null && message.hasOwnProperty("stateRequest"))
            $root.StateRequest.encode(message.stateRequest, writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified Envelope message, length delimited. Does not implicitly {@link Envelope.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Envelope
     * @static
     * @param {IEnvelope} message Envelope message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Envelope.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes an Envelope message from the specified reader or buffer.
     * @function decode
     * @memberof Envelope
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Envelope} Envelope
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Envelope.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Envelope();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.acknowledged = $root.Envelope.Acknowledged.decode(reader, reader.uint32());
                break;
            case 2:
                message.simulationEvent = $root.StateModificationEvent.decode(reader, reader.uint32());
                break;
            case 3:
                message.connectivityEvents = $root.ConnectivityEvents.decode(reader, reader.uint32());
                break;
            case 4:
                message.stateUpdate = $root.StateUpdate.decode(reader, reader.uint32());
                break;
            case 5:
                message.stateRequest = $root.StateRequest.decode(reader, reader.uint32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes an Envelope message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Envelope
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Envelope} Envelope
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Envelope.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies an Envelope message.
     * @function verify
     * @memberof Envelope
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Envelope.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        let properties = {};
        if (message.acknowledged != null && message.hasOwnProperty("acknowledged")) {
            properties.message = 1;
            {
                let error = $root.Envelope.Acknowledged.verify(message.acknowledged);
                if (error)
                    return "acknowledged." + error;
            }
        }
        if (message.simulationEvent != null && message.hasOwnProperty("simulationEvent")) {
            if (properties.message === 1)
                return "message: multiple values";
            properties.message = 1;
            {
                let error = $root.StateModificationEvent.verify(message.simulationEvent);
                if (error)
                    return "simulationEvent." + error;
            }
        }
        if (message.connectivityEvents != null && message.hasOwnProperty("connectivityEvents")) {
            if (properties.message === 1)
                return "message: multiple values";
            properties.message = 1;
            {
                let error = $root.ConnectivityEvents.verify(message.connectivityEvents);
                if (error)
                    return "connectivityEvents." + error;
            }
        }
        if (message.stateUpdate != null && message.hasOwnProperty("stateUpdate")) {
            if (properties.message === 1)
                return "message: multiple values";
            properties.message = 1;
            {
                let error = $root.StateUpdate.verify(message.stateUpdate);
                if (error)
                    return "stateUpdate." + error;
            }
        }
        if (message.stateRequest != null && message.hasOwnProperty("stateRequest")) {
            if (properties.message === 1)
                return "message: multiple values";
            properties.message = 1;
            {
                let error = $root.StateRequest.verify(message.stateRequest);
                if (error)
                    return "stateRequest." + error;
            }
        }
        return null;
    };

    /**
     * Creates an Envelope message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Envelope
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Envelope} Envelope
     */
    Envelope.fromObject = function fromObject(object) {
        if (object instanceof $root.Envelope)
            return object;
        let message = new $root.Envelope();
        if (object.acknowledged != null) {
            if (typeof object.acknowledged !== "object")
                throw TypeError(".Envelope.acknowledged: object expected");
            message.acknowledged = $root.Envelope.Acknowledged.fromObject(object.acknowledged);
        }
        if (object.simulationEvent != null) {
            if (typeof object.simulationEvent !== "object")
                throw TypeError(".Envelope.simulationEvent: object expected");
            message.simulationEvent = $root.StateModificationEvent.fromObject(object.simulationEvent);
        }
        if (object.connectivityEvents != null) {
            if (typeof object.connectivityEvents !== "object")
                throw TypeError(".Envelope.connectivityEvents: object expected");
            message.connectivityEvents = $root.ConnectivityEvents.fromObject(object.connectivityEvents);
        }
        if (object.stateUpdate != null) {
            if (typeof object.stateUpdate !== "object")
                throw TypeError(".Envelope.stateUpdate: object expected");
            message.stateUpdate = $root.StateUpdate.fromObject(object.stateUpdate);
        }
        if (object.stateRequest != null) {
            if (typeof object.stateRequest !== "object")
                throw TypeError(".Envelope.stateRequest: object expected");
            message.stateRequest = $root.StateRequest.fromObject(object.stateRequest);
        }
        return message;
    };

    /**
     * Creates a plain object from an Envelope message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Envelope
     * @static
     * @param {Envelope} message Envelope
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Envelope.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (message.acknowledged != null && message.hasOwnProperty("acknowledged")) {
            object.acknowledged = $root.Envelope.Acknowledged.toObject(message.acknowledged, options);
            if (options.oneofs)
                object.message = "acknowledged";
        }
        if (message.simulationEvent != null && message.hasOwnProperty("simulationEvent")) {
            object.simulationEvent = $root.StateModificationEvent.toObject(message.simulationEvent, options);
            if (options.oneofs)
                object.message = "simulationEvent";
        }
        if (message.connectivityEvents != null && message.hasOwnProperty("connectivityEvents")) {
            object.connectivityEvents = $root.ConnectivityEvents.toObject(message.connectivityEvents, options);
            if (options.oneofs)
                object.message = "connectivityEvents";
        }
        if (message.stateUpdate != null && message.hasOwnProperty("stateUpdate")) {
            object.stateUpdate = $root.StateUpdate.toObject(message.stateUpdate, options);
            if (options.oneofs)
                object.message = "stateUpdate";
        }
        if (message.stateRequest != null && message.hasOwnProperty("stateRequest")) {
            object.stateRequest = $root.StateRequest.toObject(message.stateRequest, options);
            if (options.oneofs)
                object.message = "stateRequest";
        }
        return object;
    };

    /**
     * Converts this Envelope to JSON.
     * @function toJSON
     * @memberof Envelope
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Envelope.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    Envelope.Acknowledged = (function() {

        /**
         * Properties of an Acknowledged.
         * @memberof Envelope
         * @interface IAcknowledged
         */

        /**
         * Constructs a new Acknowledged.
         * @memberof Envelope
         * @classdesc Represents an Acknowledged.
         * @implements IAcknowledged
         * @constructor
         * @param {Envelope.IAcknowledged=} [properties] Properties to set
         */
        function Acknowledged(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * Creates a new Acknowledged instance using the specified properties.
         * @function create
         * @memberof Envelope.Acknowledged
         * @static
         * @param {Envelope.IAcknowledged=} [properties] Properties to set
         * @returns {Envelope.Acknowledged} Acknowledged instance
         */
        Acknowledged.create = function create(properties) {
            return new Acknowledged(properties);
        };

        /**
         * Encodes the specified Acknowledged message. Does not implicitly {@link Envelope.Acknowledged.verify|verify} messages.
         * @function encode
         * @memberof Envelope.Acknowledged
         * @static
         * @param {Envelope.IAcknowledged} message Acknowledged message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Acknowledged.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            return writer;
        };

        /**
         * Encodes the specified Acknowledged message, length delimited. Does not implicitly {@link Envelope.Acknowledged.verify|verify} messages.
         * @function encodeDelimited
         * @memberof Envelope.Acknowledged
         * @static
         * @param {Envelope.IAcknowledged} message Acknowledged message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Acknowledged.encodeDelimited = function encodeDelimited(message, writer) {
            return this.encode(message, writer).ldelim();
        };

        /**
         * Decodes an Acknowledged message from the specified reader or buffer.
         * @function decode
         * @memberof Envelope.Acknowledged
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {Envelope.Acknowledged} Acknowledged
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Acknowledged.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Envelope.Acknowledged();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        /**
         * Decodes an Acknowledged message from the specified reader or buffer, length delimited.
         * @function decodeDelimited
         * @memberof Envelope.Acknowledged
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @returns {Envelope.Acknowledged} Acknowledged
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Acknowledged.decodeDelimited = function decodeDelimited(reader) {
            if (!(reader instanceof $Reader))
                reader = new $Reader(reader);
            return this.decode(reader, reader.uint32());
        };

        /**
         * Verifies an Acknowledged message.
         * @function verify
         * @memberof Envelope.Acknowledged
         * @static
         * @param {Object.<string,*>} message Plain object to verify
         * @returns {string|null} `null` if valid, otherwise the reason why it is not
         */
        Acknowledged.verify = function verify(message) {
            if (typeof message !== "object" || message === null)
                return "object expected";
            return null;
        };

        /**
         * Creates an Acknowledged message from a plain object. Also converts values to their respective internal types.
         * @function fromObject
         * @memberof Envelope.Acknowledged
         * @static
         * @param {Object.<string,*>} object Plain object
         * @returns {Envelope.Acknowledged} Acknowledged
         */
        Acknowledged.fromObject = function fromObject(object) {
            if (object instanceof $root.Envelope.Acknowledged)
                return object;
            return new $root.Envelope.Acknowledged();
        };

        /**
         * Creates a plain object from an Acknowledged message. Also converts values to other types if specified.
         * @function toObject
         * @memberof Envelope.Acknowledged
         * @static
         * @param {Envelope.Acknowledged} message Acknowledged
         * @param {$protobuf.IConversionOptions} [options] Conversion options
         * @returns {Object.<string,*>} Plain object
         */
        Acknowledged.toObject = function toObject() {
            return {};
        };

        /**
         * Converts this Acknowledged to JSON.
         * @function toJSON
         * @memberof Envelope.Acknowledged
         * @instance
         * @returns {Object.<string,*>} JSON object
         */
        Acknowledged.prototype.toJSON = function toJSON() {
            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
        };

        return Acknowledged;
    })();

    return Envelope;
})();

export const ConnectivityEvents = $root.ConnectivityEvents = (() => {

    /**
     * Properties of a ConnectivityEvents.
     * @exports IConnectivityEvents
     * @interface IConnectivityEvents
     * @property {ConnectivityEvents.EventType|null} [type] ConnectivityEvents type
     * @property {string|null} [targetId] ConnectivityEvents targetId
     * @property {number|null} [activeClients] ConnectivityEvents activeClients
     */

    /**
     * Constructs a new ConnectivityEvents.
     * @exports ConnectivityEvents
     * @classdesc Represents a ConnectivityEvents.
     * @implements IConnectivityEvents
     * @constructor
     * @param {IConnectivityEvents=} [properties] Properties to set
     */
    function ConnectivityEvents(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * ConnectivityEvents type.
     * @member {ConnectivityEvents.EventType} type
     * @memberof ConnectivityEvents
     * @instance
     */
    ConnectivityEvents.prototype.type = 0;

    /**
     * ConnectivityEvents targetId.
     * @member {string} targetId
     * @memberof ConnectivityEvents
     * @instance
     */
    ConnectivityEvents.prototype.targetId = "";

    /**
     * ConnectivityEvents activeClients.
     * @member {number} activeClients
     * @memberof ConnectivityEvents
     * @instance
     */
    ConnectivityEvents.prototype.activeClients = 0;

    /**
     * Creates a new ConnectivityEvents instance using the specified properties.
     * @function create
     * @memberof ConnectivityEvents
     * @static
     * @param {IConnectivityEvents=} [properties] Properties to set
     * @returns {ConnectivityEvents} ConnectivityEvents instance
     */
    ConnectivityEvents.create = function create(properties) {
        return new ConnectivityEvents(properties);
    };

    /**
     * Encodes the specified ConnectivityEvents message. Does not implicitly {@link ConnectivityEvents.verify|verify} messages.
     * @function encode
     * @memberof ConnectivityEvents
     * @static
     * @param {IConnectivityEvents} message ConnectivityEvents message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    ConnectivityEvents.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.type != null && message.hasOwnProperty("type"))
            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
        if (message.targetId != null && message.hasOwnProperty("targetId"))
            writer.uint32(/* id 2, wireType 2 =*/18).string(message.targetId);
        if (message.activeClients != null && message.hasOwnProperty("activeClients"))
            writer.uint32(/* id 3, wireType 0 =*/24).uint32(message.activeClients);
        return writer;
    };

    /**
     * Encodes the specified ConnectivityEvents message, length delimited. Does not implicitly {@link ConnectivityEvents.verify|verify} messages.
     * @function encodeDelimited
     * @memberof ConnectivityEvents
     * @static
     * @param {IConnectivityEvents} message ConnectivityEvents message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    ConnectivityEvents.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a ConnectivityEvents message from the specified reader or buffer.
     * @function decode
     * @memberof ConnectivityEvents
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {ConnectivityEvents} ConnectivityEvents
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    ConnectivityEvents.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.ConnectivityEvents();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.type = reader.int32();
                break;
            case 2:
                message.targetId = reader.string();
                break;
            case 3:
                message.activeClients = reader.uint32();
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a ConnectivityEvents message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof ConnectivityEvents
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {ConnectivityEvents} ConnectivityEvents
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    ConnectivityEvents.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a ConnectivityEvents message.
     * @function verify
     * @memberof ConnectivityEvents
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    ConnectivityEvents.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.type != null && message.hasOwnProperty("type"))
            switch (message.type) {
            default:
                return "type: enum value expected";
            case 0:
            case 1:
                break;
            }
        if (message.targetId != null && message.hasOwnProperty("targetId"))
            if (!$util.isString(message.targetId))
                return "targetId: string expected";
        if (message.activeClients != null && message.hasOwnProperty("activeClients"))
            if (!$util.isInteger(message.activeClients))
                return "activeClients: integer expected";
        return null;
    };

    /**
     * Creates a ConnectivityEvents message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof ConnectivityEvents
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {ConnectivityEvents} ConnectivityEvents
     */
    ConnectivityEvents.fromObject = function fromObject(object) {
        if (object instanceof $root.ConnectivityEvents)
            return object;
        let message = new $root.ConnectivityEvents();
        switch (object.type) {
        case "ClientJoined":
        case 0:
            message.type = 0;
            break;
        case "ClientLeaved":
        case 1:
            message.type = 1;
            break;
        }
        if (object.targetId != null)
            message.targetId = String(object.targetId);
        if (object.activeClients != null)
            message.activeClients = object.activeClients >>> 0;
        return message;
    };

    /**
     * Creates a plain object from a ConnectivityEvents message. Also converts values to other types if specified.
     * @function toObject
     * @memberof ConnectivityEvents
     * @static
     * @param {ConnectivityEvents} message ConnectivityEvents
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    ConnectivityEvents.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults) {
            object.type = options.enums === String ? "ClientJoined" : 0;
            object.targetId = "";
            object.activeClients = 0;
        }
        if (message.type != null && message.hasOwnProperty("type"))
            object.type = options.enums === String ? $root.ConnectivityEvents.EventType[message.type] : message.type;
        if (message.targetId != null && message.hasOwnProperty("targetId"))
            object.targetId = message.targetId;
        if (message.activeClients != null && message.hasOwnProperty("activeClients"))
            object.activeClients = message.activeClients;
        return object;
    };

    /**
     * Converts this ConnectivityEvents to JSON.
     * @function toJSON
     * @memberof ConnectivityEvents
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    ConnectivityEvents.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    /**
     * EventType enum.
     * @name ConnectivityEvents.EventType
     * @enum {string}
     * @property {number} ClientJoined=0 ClientJoined value
     * @property {number} ClientLeaved=1 ClientLeaved value
     */
    ConnectivityEvents.EventType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "ClientJoined"] = 0;
        values[valuesById[1] = "ClientLeaved"] = 1;
        return values;
    })();

    return ConnectivityEvents;
})();

export const StateModificationEvent = $root.StateModificationEvent = (() => {

    /**
     * Properties of a StateModificationEvent.
     * @exports IStateModificationEvent
     * @interface IStateModificationEvent
     * @property {ICreateEntity|null} [create] StateModificationEvent create
     * @property {IDeleteEntity|null} ["delete"] StateModificationEvent delete
     * @property {IEditEntity|null} [edit] StateModificationEvent edit
     */

    /**
     * Constructs a new StateModificationEvent.
     * @exports StateModificationEvent
     * @classdesc Represents a StateModificationEvent.
     * @implements IStateModificationEvent
     * @constructor
     * @param {IStateModificationEvent=} [properties] Properties to set
     */
    function StateModificationEvent(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * StateModificationEvent create.
     * @member {ICreateEntity|null|undefined} create
     * @memberof StateModificationEvent
     * @instance
     */
    StateModificationEvent.prototype.create = null;

    /**
     * StateModificationEvent delete.
     * @member {IDeleteEntity|null|undefined} delete
     * @memberof StateModificationEvent
     * @instance
     */
    StateModificationEvent.prototype["delete"] = null;

    /**
     * StateModificationEvent edit.
     * @member {IEditEntity|null|undefined} edit
     * @memberof StateModificationEvent
     * @instance
     */
    StateModificationEvent.prototype.edit = null;

    // OneOf field names bound to virtual getters and setters
    let $oneOfFields;

    /**
     * StateModificationEvent command.
     * @member {"create"|"delete"|"edit"|undefined} command
     * @memberof StateModificationEvent
     * @instance
     */
    Object.defineProperty(StateModificationEvent.prototype, "command", {
        get: $util.oneOfGetter($oneOfFields = ["create", "delete", "edit"]),
        set: $util.oneOfSetter($oneOfFields)
    });

    /**
     * Creates a new StateModificationEvent instance using the specified properties.
     * @function create
     * @memberof StateModificationEvent
     * @static
     * @param {IStateModificationEvent=} [properties] Properties to set
     * @returns {StateModificationEvent} StateModificationEvent instance
     */
    StateModificationEvent.create = function create(properties) {
        return new StateModificationEvent(properties);
    };

    /**
     * Encodes the specified StateModificationEvent message. Does not implicitly {@link StateModificationEvent.verify|verify} messages.
     * @function encode
     * @memberof StateModificationEvent
     * @static
     * @param {IStateModificationEvent} message StateModificationEvent message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    StateModificationEvent.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.create != null && message.hasOwnProperty("create"))
            $root.CreateEntity.encode(message.create, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
        if (message["delete"] != null && message.hasOwnProperty("delete"))
            $root.DeleteEntity.encode(message["delete"], writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        if (message.edit != null && message.hasOwnProperty("edit"))
            $root.EditEntity.encode(message.edit, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified StateModificationEvent message, length delimited. Does not implicitly {@link StateModificationEvent.verify|verify} messages.
     * @function encodeDelimited
     * @memberof StateModificationEvent
     * @static
     * @param {IStateModificationEvent} message StateModificationEvent message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    StateModificationEvent.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a StateModificationEvent message from the specified reader or buffer.
     * @function decode
     * @memberof StateModificationEvent
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {StateModificationEvent} StateModificationEvent
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    StateModificationEvent.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.StateModificationEvent();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.create = $root.CreateEntity.decode(reader, reader.uint32());
                break;
            case 2:
                message["delete"] = $root.DeleteEntity.decode(reader, reader.uint32());
                break;
            case 3:
                message.edit = $root.EditEntity.decode(reader, reader.uint32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a StateModificationEvent message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof StateModificationEvent
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {StateModificationEvent} StateModificationEvent
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    StateModificationEvent.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a StateModificationEvent message.
     * @function verify
     * @memberof StateModificationEvent
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    StateModificationEvent.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        let properties = {};
        if (message.create != null && message.hasOwnProperty("create")) {
            properties.command = 1;
            {
                let error = $root.CreateEntity.verify(message.create);
                if (error)
                    return "create." + error;
            }
        }
        if (message["delete"] != null && message.hasOwnProperty("delete")) {
            if (properties.command === 1)
                return "command: multiple values";
            properties.command = 1;
            {
                let error = $root.DeleteEntity.verify(message["delete"]);
                if (error)
                    return "delete." + error;
            }
        }
        if (message.edit != null && message.hasOwnProperty("edit")) {
            if (properties.command === 1)
                return "command: multiple values";
            properties.command = 1;
            {
                let error = $root.EditEntity.verify(message.edit);
                if (error)
                    return "edit." + error;
            }
        }
        return null;
    };

    /**
     * Creates a StateModificationEvent message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof StateModificationEvent
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {StateModificationEvent} StateModificationEvent
     */
    StateModificationEvent.fromObject = function fromObject(object) {
        if (object instanceof $root.StateModificationEvent)
            return object;
        let message = new $root.StateModificationEvent();
        if (object.create != null) {
            if (typeof object.create !== "object")
                throw TypeError(".StateModificationEvent.create: object expected");
            message.create = $root.CreateEntity.fromObject(object.create);
        }
        if (object["delete"] != null) {
            if (typeof object["delete"] !== "object")
                throw TypeError(".StateModificationEvent.delete: object expected");
            message["delete"] = $root.DeleteEntity.fromObject(object["delete"]);
        }
        if (object.edit != null) {
            if (typeof object.edit !== "object")
                throw TypeError(".StateModificationEvent.edit: object expected");
            message.edit = $root.EditEntity.fromObject(object.edit);
        }
        return message;
    };

    /**
     * Creates a plain object from a StateModificationEvent message. Also converts values to other types if specified.
     * @function toObject
     * @memberof StateModificationEvent
     * @static
     * @param {StateModificationEvent} message StateModificationEvent
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    StateModificationEvent.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (message.create != null && message.hasOwnProperty("create")) {
            object.create = $root.CreateEntity.toObject(message.create, options);
            if (options.oneofs)
                object.command = "create";
        }
        if (message["delete"] != null && message.hasOwnProperty("delete")) {
            object["delete"] = $root.DeleteEntity.toObject(message["delete"], options);
            if (options.oneofs)
                object.command = "delete";
        }
        if (message.edit != null && message.hasOwnProperty("edit")) {
            object.edit = $root.EditEntity.toObject(message.edit, options);
            if (options.oneofs)
                object.command = "edit";
        }
        return object;
    };

    /**
     * Converts this StateModificationEvent to JSON.
     * @function toJSON
     * @memberof StateModificationEvent
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    StateModificationEvent.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return StateModificationEvent;
})();

export const StateRequest = $root.StateRequest = (() => {

    /**
     * Properties of a StateRequest.
     * @exports IStateRequest
     * @interface IStateRequest
     * @property {StateUpdate.UpdateType|null} [type] StateRequest type
     */

    /**
     * Constructs a new StateRequest.
     * @exports StateRequest
     * @classdesc Represents a StateRequest.
     * @implements IStateRequest
     * @constructor
     * @param {IStateRequest=} [properties] Properties to set
     */
    function StateRequest(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * StateRequest type.
     * @member {StateUpdate.UpdateType} type
     * @memberof StateRequest
     * @instance
     */
    StateRequest.prototype.type = 0;

    /**
     * Creates a new StateRequest instance using the specified properties.
     * @function create
     * @memberof StateRequest
     * @static
     * @param {IStateRequest=} [properties] Properties to set
     * @returns {StateRequest} StateRequest instance
     */
    StateRequest.create = function create(properties) {
        return new StateRequest(properties);
    };

    /**
     * Encodes the specified StateRequest message. Does not implicitly {@link StateRequest.verify|verify} messages.
     * @function encode
     * @memberof StateRequest
     * @static
     * @param {IStateRequest} message StateRequest message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    StateRequest.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.type != null && message.hasOwnProperty("type"))
            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.type);
        return writer;
    };

    /**
     * Encodes the specified StateRequest message, length delimited. Does not implicitly {@link StateRequest.verify|verify} messages.
     * @function encodeDelimited
     * @memberof StateRequest
     * @static
     * @param {IStateRequest} message StateRequest message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    StateRequest.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a StateRequest message from the specified reader or buffer.
     * @function decode
     * @memberof StateRequest
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {StateRequest} StateRequest
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    StateRequest.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.StateRequest();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.type = reader.int32();
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a StateRequest message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof StateRequest
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {StateRequest} StateRequest
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    StateRequest.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a StateRequest message.
     * @function verify
     * @memberof StateRequest
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    StateRequest.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.type != null && message.hasOwnProperty("type"))
            switch (message.type) {
            default:
                return "type: enum value expected";
            case 0:
            case 1:
                break;
            }
        return null;
    };

    /**
     * Creates a StateRequest message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof StateRequest
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {StateRequest} StateRequest
     */
    StateRequest.fromObject = function fromObject(object) {
        if (object instanceof $root.StateRequest)
            return object;
        let message = new $root.StateRequest();
        switch (object.type) {
        case "Delta":
        case 0:
            message.type = 0;
            break;
        case "Full":
        case 1:
            message.type = 1;
            break;
        }
        return message;
    };

    /**
     * Creates a plain object from a StateRequest message. Also converts values to other types if specified.
     * @function toObject
     * @memberof StateRequest
     * @static
     * @param {StateRequest} message StateRequest
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    StateRequest.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults)
            object.type = options.enums === String ? "Delta" : 0;
        if (message.type != null && message.hasOwnProperty("type"))
            object.type = options.enums === String ? $root.StateUpdate.UpdateType[message.type] : message.type;
        return object;
    };

    /**
     * Converts this StateRequest to JSON.
     * @function toJSON
     * @memberof StateRequest
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    StateRequest.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return StateRequest;
})();

export const CreateEntity = $root.CreateEntity = (() => {

    /**
     * Properties of a CreateEntity.
     * @exports ICreateEntity
     * @interface ICreateEntity
     */

    /**
     * Constructs a new CreateEntity.
     * @exports CreateEntity
     * @classdesc Represents a CreateEntity.
     * @implements ICreateEntity
     * @constructor
     * @param {ICreateEntity=} [properties] Properties to set
     */
    function CreateEntity(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Creates a new CreateEntity instance using the specified properties.
     * @function create
     * @memberof CreateEntity
     * @static
     * @param {ICreateEntity=} [properties] Properties to set
     * @returns {CreateEntity} CreateEntity instance
     */
    CreateEntity.create = function create(properties) {
        return new CreateEntity(properties);
    };

    /**
     * Encodes the specified CreateEntity message. Does not implicitly {@link CreateEntity.verify|verify} messages.
     * @function encode
     * @memberof CreateEntity
     * @static
     * @param {ICreateEntity} message CreateEntity message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    CreateEntity.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        return writer;
    };

    /**
     * Encodes the specified CreateEntity message, length delimited. Does not implicitly {@link CreateEntity.verify|verify} messages.
     * @function encodeDelimited
     * @memberof CreateEntity
     * @static
     * @param {ICreateEntity} message CreateEntity message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    CreateEntity.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a CreateEntity message from the specified reader or buffer.
     * @function decode
     * @memberof CreateEntity
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {CreateEntity} CreateEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    CreateEntity.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.CreateEntity();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a CreateEntity message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof CreateEntity
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {CreateEntity} CreateEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    CreateEntity.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a CreateEntity message.
     * @function verify
     * @memberof CreateEntity
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    CreateEntity.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        return null;
    };

    /**
     * Creates a CreateEntity message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof CreateEntity
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {CreateEntity} CreateEntity
     */
    CreateEntity.fromObject = function fromObject(object) {
        if (object instanceof $root.CreateEntity)
            return object;
        return new $root.CreateEntity();
    };

    /**
     * Creates a plain object from a CreateEntity message. Also converts values to other types if specified.
     * @function toObject
     * @memberof CreateEntity
     * @static
     * @param {CreateEntity} message CreateEntity
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    CreateEntity.toObject = function toObject() {
        return {};
    };

    /**
     * Converts this CreateEntity to JSON.
     * @function toJSON
     * @memberof CreateEntity
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    CreateEntity.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return CreateEntity;
})();

export const DeleteEntity = $root.DeleteEntity = (() => {

    /**
     * Properties of a DeleteEntity.
     * @exports IDeleteEntity
     * @interface IDeleteEntity
     */

    /**
     * Constructs a new DeleteEntity.
     * @exports DeleteEntity
     * @classdesc Represents a DeleteEntity.
     * @implements IDeleteEntity
     * @constructor
     * @param {IDeleteEntity=} [properties] Properties to set
     */
    function DeleteEntity(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Creates a new DeleteEntity instance using the specified properties.
     * @function create
     * @memberof DeleteEntity
     * @static
     * @param {IDeleteEntity=} [properties] Properties to set
     * @returns {DeleteEntity} DeleteEntity instance
     */
    DeleteEntity.create = function create(properties) {
        return new DeleteEntity(properties);
    };

    /**
     * Encodes the specified DeleteEntity message. Does not implicitly {@link DeleteEntity.verify|verify} messages.
     * @function encode
     * @memberof DeleteEntity
     * @static
     * @param {IDeleteEntity} message DeleteEntity message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    DeleteEntity.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        return writer;
    };

    /**
     * Encodes the specified DeleteEntity message, length delimited. Does not implicitly {@link DeleteEntity.verify|verify} messages.
     * @function encodeDelimited
     * @memberof DeleteEntity
     * @static
     * @param {IDeleteEntity} message DeleteEntity message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    DeleteEntity.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a DeleteEntity message from the specified reader or buffer.
     * @function decode
     * @memberof DeleteEntity
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {DeleteEntity} DeleteEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    DeleteEntity.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.DeleteEntity();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a DeleteEntity message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof DeleteEntity
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {DeleteEntity} DeleteEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    DeleteEntity.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a DeleteEntity message.
     * @function verify
     * @memberof DeleteEntity
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    DeleteEntity.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        return null;
    };

    /**
     * Creates a DeleteEntity message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof DeleteEntity
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {DeleteEntity} DeleteEntity
     */
    DeleteEntity.fromObject = function fromObject(object) {
        if (object instanceof $root.DeleteEntity)
            return object;
        return new $root.DeleteEntity();
    };

    /**
     * Creates a plain object from a DeleteEntity message. Also converts values to other types if specified.
     * @function toObject
     * @memberof DeleteEntity
     * @static
     * @param {DeleteEntity} message DeleteEntity
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    DeleteEntity.toObject = function toObject() {
        return {};
    };

    /**
     * Converts this DeleteEntity to JSON.
     * @function toJSON
     * @memberof DeleteEntity
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    DeleteEntity.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return DeleteEntity;
})();

export const EditEntity = $root.EditEntity = (() => {

    /**
     * Properties of an EditEntity.
     * @exports IEditEntity
     * @interface IEditEntity
     */

    /**
     * Constructs a new EditEntity.
     * @exports EditEntity
     * @classdesc Represents an EditEntity.
     * @implements IEditEntity
     * @constructor
     * @param {IEditEntity=} [properties] Properties to set
     */
    function EditEntity(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Creates a new EditEntity instance using the specified properties.
     * @function create
     * @memberof EditEntity
     * @static
     * @param {IEditEntity=} [properties] Properties to set
     * @returns {EditEntity} EditEntity instance
     */
    EditEntity.create = function create(properties) {
        return new EditEntity(properties);
    };

    /**
     * Encodes the specified EditEntity message. Does not implicitly {@link EditEntity.verify|verify} messages.
     * @function encode
     * @memberof EditEntity
     * @static
     * @param {IEditEntity} message EditEntity message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    EditEntity.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        return writer;
    };

    /**
     * Encodes the specified EditEntity message, length delimited. Does not implicitly {@link EditEntity.verify|verify} messages.
     * @function encodeDelimited
     * @memberof EditEntity
     * @static
     * @param {IEditEntity} message EditEntity message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    EditEntity.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes an EditEntity message from the specified reader or buffer.
     * @function decode
     * @memberof EditEntity
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {EditEntity} EditEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    EditEntity.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.EditEntity();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes an EditEntity message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof EditEntity
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {EditEntity} EditEntity
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    EditEntity.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies an EditEntity message.
     * @function verify
     * @memberof EditEntity
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    EditEntity.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        return null;
    };

    /**
     * Creates an EditEntity message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof EditEntity
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {EditEntity} EditEntity
     */
    EditEntity.fromObject = function fromObject(object) {
        if (object instanceof $root.EditEntity)
            return object;
        return new $root.EditEntity();
    };

    /**
     * Creates a plain object from an EditEntity message. Also converts values to other types if specified.
     * @function toObject
     * @memberof EditEntity
     * @static
     * @param {EditEntity} message EditEntity
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    EditEntity.toObject = function toObject() {
        return {};
    };

    /**
     * Converts this EditEntity to JSON.
     * @function toJSON
     * @memberof EditEntity
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    EditEntity.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return EditEntity;
})();

export const StateUpdate = $root.StateUpdate = (() => {

    /**
     * Properties of a StateUpdate.
     * @exports IStateUpdate
     * @interface IStateUpdate
     * @property {StateUpdate.UpdateType|null} [updateType] StateUpdate updateType
     * @property {number|Long|null} [timestamp] StateUpdate timestamp
     * @property {StateUpdate.ICreated|null} [created] StateUpdate created
     * @property {StateUpdate.IUpdated|null} [updated] StateUpdate updated
     * @property {StateUpdate.IDeleted|null} [deleted] StateUpdate deleted
     * @property {StateUpdate.IUpdateMeta|null} [meta] StateUpdate meta
     */

    /**
     * Constructs a new StateUpdate.
     * @exports StateUpdate
     * @classdesc Represents a StateUpdate.
     * @implements IStateUpdate
     * @constructor
     * @param {IStateUpdate=} [properties] Properties to set
     */
    function StateUpdate(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * StateUpdate updateType.
     * @member {StateUpdate.UpdateType} updateType
     * @memberof StateUpdate
     * @instance
     */
    StateUpdate.prototype.updateType = 0;

    /**
     * StateUpdate timestamp.
     * @member {number|Long} timestamp
     * @memberof StateUpdate
     * @instance
     */
    StateUpdate.prototype.timestamp = $util.Long ? $util.Long.fromBits(0,0,true) : 0;

    /**
     * StateUpdate created.
     * @member {StateUpdate.ICreated|null|undefined} created
     * @memberof StateUpdate
     * @instance
     */
    StateUpdate.prototype.created = null;

    /**
     * StateUpdate updated.
     * @member {StateUpdate.IUpdated|null|undefined} updated
     * @memberof StateUpdate
     * @instance
     */
    StateUpdate.prototype.updated = null;

    /**
     * StateUpdate deleted.
     * @member {StateUpdate.IDeleted|null|undefined} deleted
     * @memberof StateUpdate
     * @instance
     */
    StateUpdate.prototype.deleted = null;

    /**
     * StateUpdate meta.
     * @member {StateUpdate.IUpdateMeta|null|undefined} meta
     * @memberof StateUpdate
     * @instance
     */
    StateUpdate.prototype.meta = null;

    /**
     * Creates a new StateUpdate instance using the specified properties.
     * @function create
     * @memberof StateUpdate
     * @static
     * @param {IStateUpdate=} [properties] Properties to set
     * @returns {StateUpdate} StateUpdate instance
     */
    StateUpdate.create = function create(properties) {
        return new StateUpdate(properties);
    };

    /**
     * Encodes the specified StateUpdate message. Does not implicitly {@link StateUpdate.verify|verify} messages.
     * @function encode
     * @memberof StateUpdate
     * @static
     * @param {IStateUpdate} message StateUpdate message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    StateUpdate.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.updateType != null && message.hasOwnProperty("updateType"))
            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.updateType);
        if (message.timestamp != null && message.hasOwnProperty("timestamp"))
            writer.uint32(/* id 2, wireType 0 =*/16).uint64(message.timestamp);
        if (message.created != null && message.hasOwnProperty("created"))
            $root.StateUpdate.Created.encode(message.created, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
        if (message.updated != null && message.hasOwnProperty("updated"))
            $root.StateUpdate.Updated.encode(message.updated, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
        if (message.deleted != null && message.hasOwnProperty("deleted"))
            $root.StateUpdate.Deleted.encode(message.deleted, writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
        if (message.meta != null && message.hasOwnProperty("meta"))
            $root.StateUpdate.UpdateMeta.encode(message.meta, writer.uint32(/* id 15, wireType 2 =*/122).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified StateUpdate message, length delimited. Does not implicitly {@link StateUpdate.verify|verify} messages.
     * @function encodeDelimited
     * @memberof StateUpdate
     * @static
     * @param {IStateUpdate} message StateUpdate message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    StateUpdate.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a StateUpdate message from the specified reader or buffer.
     * @function decode
     * @memberof StateUpdate
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {StateUpdate} StateUpdate
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    StateUpdate.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.StateUpdate();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.updateType = reader.int32();
                break;
            case 2:
                message.timestamp = reader.uint64();
                break;
            case 3:
                message.created = $root.StateUpdate.Created.decode(reader, reader.uint32());
                break;
            case 4:
                message.updated = $root.StateUpdate.Updated.decode(reader, reader.uint32());
                break;
            case 5:
                message.deleted = $root.StateUpdate.Deleted.decode(reader, reader.uint32());
                break;
            case 15:
                message.meta = $root.StateUpdate.UpdateMeta.decode(reader, reader.uint32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a StateUpdate message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof StateUpdate
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {StateUpdate} StateUpdate
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    StateUpdate.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a StateUpdate message.
     * @function verify
     * @memberof StateUpdate
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    StateUpdate.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.updateType != null && message.hasOwnProperty("updateType"))
            switch (message.updateType) {
            default:
                return "updateType: enum value expected";
            case 0:
            case 1:
                break;
            }
        if (message.timestamp != null && message.hasOwnProperty("timestamp"))
            if (!$util.isInteger(message.timestamp) && !(message.timestamp && $util.isInteger(message.timestamp.low) && $util.isInteger(message.timestamp.high)))
                return "timestamp: integer|Long expected";
        if (message.created != null && message.hasOwnProperty("created")) {
            let error = $root.StateUpdate.Created.verify(message.created);
            if (error)
                return "created." + error;
        }
        if (message.updated != null && message.hasOwnProperty("updated")) {
            let error = $root.StateUpdate.Updated.verify(message.updated);
            if (error)
                return "updated." + error;
        }
        if (message.deleted != null && message.hasOwnProperty("deleted")) {
            let error = $root.StateUpdate.Deleted.verify(message.deleted);
            if (error)
                return "deleted." + error;
        }
        if (message.meta != null && message.hasOwnProperty("meta")) {
            let error = $root.StateUpdate.UpdateMeta.verify(message.meta);
            if (error)
                return "meta." + error;
        }
        return null;
    };

    /**
     * Creates a StateUpdate message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof StateUpdate
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {StateUpdate} StateUpdate
     */
    StateUpdate.fromObject = function fromObject(object) {
        if (object instanceof $root.StateUpdate)
            return object;
        let message = new $root.StateUpdate();
        switch (object.updateType) {
        case "Delta":
        case 0:
            message.updateType = 0;
            break;
        case "Full":
        case 1:
            message.updateType = 1;
            break;
        }
        if (object.timestamp != null)
            if ($util.Long)
                (message.timestamp = $util.Long.fromValue(object.timestamp)).unsigned = true;
            else if (typeof object.timestamp === "string")
                message.timestamp = parseInt(object.timestamp, 10);
            else if (typeof object.timestamp === "number")
                message.timestamp = object.timestamp;
            else if (typeof object.timestamp === "object")
                message.timestamp = new $util.LongBits(object.timestamp.low >>> 0, object.timestamp.high >>> 0).toNumber(true);
        if (object.created != null) {
            if (typeof object.created !== "object")
                throw TypeError(".StateUpdate.created: object expected");
            message.created = $root.StateUpdate.Created.fromObject(object.created);
        }
        if (object.updated != null) {
            if (typeof object.updated !== "object")
                throw TypeError(".StateUpdate.updated: object expected");
            message.updated = $root.StateUpdate.Updated.fromObject(object.updated);
        }
        if (object.deleted != null) {
            if (typeof object.deleted !== "object")
                throw TypeError(".StateUpdate.deleted: object expected");
            message.deleted = $root.StateUpdate.Deleted.fromObject(object.deleted);
        }
        if (object.meta != null) {
            if (typeof object.meta !== "object")
                throw TypeError(".StateUpdate.meta: object expected");
            message.meta = $root.StateUpdate.UpdateMeta.fromObject(object.meta);
        }
        return message;
    };

    /**
     * Creates a plain object from a StateUpdate message. Also converts values to other types if specified.
     * @function toObject
     * @memberof StateUpdate
     * @static
     * @param {StateUpdate} message StateUpdate
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    StateUpdate.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults) {
            object.updateType = options.enums === String ? "Delta" : 0;
            if ($util.Long) {
                let long = new $util.Long(0, 0, true);
                object.timestamp = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
            } else
                object.timestamp = options.longs === String ? "0" : 0;
            object.created = null;
            object.updated = null;
            object.deleted = null;
            object.meta = null;
        }
        if (message.updateType != null && message.hasOwnProperty("updateType"))
            object.updateType = options.enums === String ? $root.StateUpdate.UpdateType[message.updateType] : message.updateType;
        if (message.timestamp != null && message.hasOwnProperty("timestamp"))
            if (typeof message.timestamp === "number")
                object.timestamp = options.longs === String ? String(message.timestamp) : message.timestamp;
            else
                object.timestamp = options.longs === String ? $util.Long.prototype.toString.call(message.timestamp) : options.longs === Number ? new $util.LongBits(message.timestamp.low >>> 0, message.timestamp.high >>> 0).toNumber(true) : message.timestamp;
        if (message.created != null && message.hasOwnProperty("created"))
            object.created = $root.StateUpdate.Created.toObject(message.created, options);
        if (message.updated != null && message.hasOwnProperty("updated"))
            object.updated = $root.StateUpdate.Updated.toObject(message.updated, options);
        if (message.deleted != null && message.hasOwnProperty("deleted"))
            object.deleted = $root.StateUpdate.Deleted.toObject(message.deleted, options);
        if (message.meta != null && message.hasOwnProperty("meta"))
            object.meta = $root.StateUpdate.UpdateMeta.toObject(message.meta, options);
        return object;
    };

    /**
     * Converts this StateUpdate to JSON.
     * @function toJSON
     * @memberof StateUpdate
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    StateUpdate.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    StateUpdate.Created = (function() {

        /**
         * Properties of a Created.
         * @memberof StateUpdate
         * @interface ICreated
         * @property {Array.<IVehicle>|null} [vehicles] Created vehicles
         * @property {Array.<IRoad>|null} [roads] Created roads
         * @property {Array.<IIntersection>|null} [intersections] Created intersections
         */

        /**
         * Constructs a new Created.
         * @memberof StateUpdate
         * @classdesc Represents a Created.
         * @implements ICreated
         * @constructor
         * @param {StateUpdate.ICreated=} [properties] Properties to set
         */
        function Created(properties) {
            this.vehicles = [];
            this.roads = [];
            this.intersections = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * Created vehicles.
         * @member {Array.<IVehicle>} vehicles
         * @memberof StateUpdate.Created
         * @instance
         */
        Created.prototype.vehicles = $util.emptyArray;

        /**
         * Created roads.
         * @member {Array.<IRoad>} roads
         * @memberof StateUpdate.Created
         * @instance
         */
        Created.prototype.roads = $util.emptyArray;

        /**
         * Created intersections.
         * @member {Array.<IIntersection>} intersections
         * @memberof StateUpdate.Created
         * @instance
         */
        Created.prototype.intersections = $util.emptyArray;

        /**
         * Creates a new Created instance using the specified properties.
         * @function create
         * @memberof StateUpdate.Created
         * @static
         * @param {StateUpdate.ICreated=} [properties] Properties to set
         * @returns {StateUpdate.Created} Created instance
         */
        Created.create = function create(properties) {
            return new Created(properties);
        };

        /**
         * Encodes the specified Created message. Does not implicitly {@link StateUpdate.Created.verify|verify} messages.
         * @function encode
         * @memberof StateUpdate.Created
         * @static
         * @param {StateUpdate.ICreated} message Created message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Created.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.vehicles != null && message.vehicles.length)
                for (let i = 0; i < message.vehicles.length; ++i)
                    $root.Vehicle.encode(message.vehicles[i], writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            if (message.roads != null && message.roads.length)
                for (let i = 0; i < message.roads.length; ++i)
                    $root.Road.encode(message.roads[i], writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
            if (message.intersections != null && message.intersections.length)
                for (let i = 0; i < message.intersections.length; ++i)
                    $root.Intersection.encode(message.intersections[i], writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
            return writer;
        };

        /**
         * Encodes the specified Created message, length delimited. Does not implicitly {@link StateUpdate.Created.verify|verify} messages.
         * @function encodeDelimited
         * @memberof StateUpdate.Created
         * @static
         * @param {StateUpdate.ICreated} message Created message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Created.encodeDelimited = function encodeDelimited(message, writer) {
            return this.encode(message, writer).ldelim();
        };

        /**
         * Decodes a Created message from the specified reader or buffer.
         * @function decode
         * @memberof StateUpdate.Created
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {StateUpdate.Created} Created
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Created.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.StateUpdate.Created();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    if (!(message.vehicles && message.vehicles.length))
                        message.vehicles = [];
                    message.vehicles.push($root.Vehicle.decode(reader, reader.uint32()));
                    break;
                case 2:
                    if (!(message.roads && message.roads.length))
                        message.roads = [];
                    message.roads.push($root.Road.decode(reader, reader.uint32()));
                    break;
                case 3:
                    if (!(message.intersections && message.intersections.length))
                        message.intersections = [];
                    message.intersections.push($root.Intersection.decode(reader, reader.uint32()));
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        /**
         * Decodes a Created message from the specified reader or buffer, length delimited.
         * @function decodeDelimited
         * @memberof StateUpdate.Created
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @returns {StateUpdate.Created} Created
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Created.decodeDelimited = function decodeDelimited(reader) {
            if (!(reader instanceof $Reader))
                reader = new $Reader(reader);
            return this.decode(reader, reader.uint32());
        };

        /**
         * Verifies a Created message.
         * @function verify
         * @memberof StateUpdate.Created
         * @static
         * @param {Object.<string,*>} message Plain object to verify
         * @returns {string|null} `null` if valid, otherwise the reason why it is not
         */
        Created.verify = function verify(message) {
            if (typeof message !== "object" || message === null)
                return "object expected";
            if (message.vehicles != null && message.hasOwnProperty("vehicles")) {
                if (!Array.isArray(message.vehicles))
                    return "vehicles: array expected";
                for (let i = 0; i < message.vehicles.length; ++i) {
                    let error = $root.Vehicle.verify(message.vehicles[i]);
                    if (error)
                        return "vehicles." + error;
                }
            }
            if (message.roads != null && message.hasOwnProperty("roads")) {
                if (!Array.isArray(message.roads))
                    return "roads: array expected";
                for (let i = 0; i < message.roads.length; ++i) {
                    let error = $root.Road.verify(message.roads[i]);
                    if (error)
                        return "roads." + error;
                }
            }
            if (message.intersections != null && message.hasOwnProperty("intersections")) {
                if (!Array.isArray(message.intersections))
                    return "intersections: array expected";
                for (let i = 0; i < message.intersections.length; ++i) {
                    let error = $root.Intersection.verify(message.intersections[i]);
                    if (error)
                        return "intersections." + error;
                }
            }
            return null;
        };

        /**
         * Creates a Created message from a plain object. Also converts values to their respective internal types.
         * @function fromObject
         * @memberof StateUpdate.Created
         * @static
         * @param {Object.<string,*>} object Plain object
         * @returns {StateUpdate.Created} Created
         */
        Created.fromObject = function fromObject(object) {
            if (object instanceof $root.StateUpdate.Created)
                return object;
            let message = new $root.StateUpdate.Created();
            if (object.vehicles) {
                if (!Array.isArray(object.vehicles))
                    throw TypeError(".StateUpdate.Created.vehicles: array expected");
                message.vehicles = [];
                for (let i = 0; i < object.vehicles.length; ++i) {
                    if (typeof object.vehicles[i] !== "object")
                        throw TypeError(".StateUpdate.Created.vehicles: object expected");
                    message.vehicles[i] = $root.Vehicle.fromObject(object.vehicles[i]);
                }
            }
            if (object.roads) {
                if (!Array.isArray(object.roads))
                    throw TypeError(".StateUpdate.Created.roads: array expected");
                message.roads = [];
                for (let i = 0; i < object.roads.length; ++i) {
                    if (typeof object.roads[i] !== "object")
                        throw TypeError(".StateUpdate.Created.roads: object expected");
                    message.roads[i] = $root.Road.fromObject(object.roads[i]);
                }
            }
            if (object.intersections) {
                if (!Array.isArray(object.intersections))
                    throw TypeError(".StateUpdate.Created.intersections: array expected");
                message.intersections = [];
                for (let i = 0; i < object.intersections.length; ++i) {
                    if (typeof object.intersections[i] !== "object")
                        throw TypeError(".StateUpdate.Created.intersections: object expected");
                    message.intersections[i] = $root.Intersection.fromObject(object.intersections[i]);
                }
            }
            return message;
        };

        /**
         * Creates a plain object from a Created message. Also converts values to other types if specified.
         * @function toObject
         * @memberof StateUpdate.Created
         * @static
         * @param {StateUpdate.Created} message Created
         * @param {$protobuf.IConversionOptions} [options] Conversion options
         * @returns {Object.<string,*>} Plain object
         */
        Created.toObject = function toObject(message, options) {
            if (!options)
                options = {};
            let object = {};
            if (options.arrays || options.defaults) {
                object.vehicles = [];
                object.roads = [];
                object.intersections = [];
            }
            if (message.vehicles && message.vehicles.length) {
                object.vehicles = [];
                for (let j = 0; j < message.vehicles.length; ++j)
                    object.vehicles[j] = $root.Vehicle.toObject(message.vehicles[j], options);
            }
            if (message.roads && message.roads.length) {
                object.roads = [];
                for (let j = 0; j < message.roads.length; ++j)
                    object.roads[j] = $root.Road.toObject(message.roads[j], options);
            }
            if (message.intersections && message.intersections.length) {
                object.intersections = [];
                for (let j = 0; j < message.intersections.length; ++j)
                    object.intersections[j] = $root.Intersection.toObject(message.intersections[j], options);
            }
            return object;
        };

        /**
         * Converts this Created to JSON.
         * @function toJSON
         * @memberof StateUpdate.Created
         * @instance
         * @returns {Object.<string,*>} JSON object
         */
        Created.prototype.toJSON = function toJSON() {
            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
        };

        return Created;
    })();

    StateUpdate.Updated = (function() {

        /**
         * Properties of an Updated.
         * @memberof StateUpdate
         * @interface IUpdated
         * @property {Array.<IVehicle>|null} [vehicles] Updated vehicles
         * @property {Array.<IRoad>|null} [roads] Updated roads
         * @property {Array.<IIntersection>|null} [intersections] Updated intersections
         */

        /**
         * Constructs a new Updated.
         * @memberof StateUpdate
         * @classdesc Represents an Updated.
         * @implements IUpdated
         * @constructor
         * @param {StateUpdate.IUpdated=} [properties] Properties to set
         */
        function Updated(properties) {
            this.vehicles = [];
            this.roads = [];
            this.intersections = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * Updated vehicles.
         * @member {Array.<IVehicle>} vehicles
         * @memberof StateUpdate.Updated
         * @instance
         */
        Updated.prototype.vehicles = $util.emptyArray;

        /**
         * Updated roads.
         * @member {Array.<IRoad>} roads
         * @memberof StateUpdate.Updated
         * @instance
         */
        Updated.prototype.roads = $util.emptyArray;

        /**
         * Updated intersections.
         * @member {Array.<IIntersection>} intersections
         * @memberof StateUpdate.Updated
         * @instance
         */
        Updated.prototype.intersections = $util.emptyArray;

        /**
         * Creates a new Updated instance using the specified properties.
         * @function create
         * @memberof StateUpdate.Updated
         * @static
         * @param {StateUpdate.IUpdated=} [properties] Properties to set
         * @returns {StateUpdate.Updated} Updated instance
         */
        Updated.create = function create(properties) {
            return new Updated(properties);
        };

        /**
         * Encodes the specified Updated message. Does not implicitly {@link StateUpdate.Updated.verify|verify} messages.
         * @function encode
         * @memberof StateUpdate.Updated
         * @static
         * @param {StateUpdate.IUpdated} message Updated message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Updated.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.vehicles != null && message.vehicles.length)
                for (let i = 0; i < message.vehicles.length; ++i)
                    $root.Vehicle.encode(message.vehicles[i], writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
            if (message.roads != null && message.roads.length)
                for (let i = 0; i < message.roads.length; ++i)
                    $root.Road.encode(message.roads[i], writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
            if (message.intersections != null && message.intersections.length)
                for (let i = 0; i < message.intersections.length; ++i)
                    $root.Intersection.encode(message.intersections[i], writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
            return writer;
        };

        /**
         * Encodes the specified Updated message, length delimited. Does not implicitly {@link StateUpdate.Updated.verify|verify} messages.
         * @function encodeDelimited
         * @memberof StateUpdate.Updated
         * @static
         * @param {StateUpdate.IUpdated} message Updated message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Updated.encodeDelimited = function encodeDelimited(message, writer) {
            return this.encode(message, writer).ldelim();
        };

        /**
         * Decodes an Updated message from the specified reader or buffer.
         * @function decode
         * @memberof StateUpdate.Updated
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {StateUpdate.Updated} Updated
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Updated.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.StateUpdate.Updated();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 3:
                    if (!(message.vehicles && message.vehicles.length))
                        message.vehicles = [];
                    message.vehicles.push($root.Vehicle.decode(reader, reader.uint32()));
                    break;
                case 4:
                    if (!(message.roads && message.roads.length))
                        message.roads = [];
                    message.roads.push($root.Road.decode(reader, reader.uint32()));
                    break;
                case 5:
                    if (!(message.intersections && message.intersections.length))
                        message.intersections = [];
                    message.intersections.push($root.Intersection.decode(reader, reader.uint32()));
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        /**
         * Decodes an Updated message from the specified reader or buffer, length delimited.
         * @function decodeDelimited
         * @memberof StateUpdate.Updated
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @returns {StateUpdate.Updated} Updated
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Updated.decodeDelimited = function decodeDelimited(reader) {
            if (!(reader instanceof $Reader))
                reader = new $Reader(reader);
            return this.decode(reader, reader.uint32());
        };

        /**
         * Verifies an Updated message.
         * @function verify
         * @memberof StateUpdate.Updated
         * @static
         * @param {Object.<string,*>} message Plain object to verify
         * @returns {string|null} `null` if valid, otherwise the reason why it is not
         */
        Updated.verify = function verify(message) {
            if (typeof message !== "object" || message === null)
                return "object expected";
            if (message.vehicles != null && message.hasOwnProperty("vehicles")) {
                if (!Array.isArray(message.vehicles))
                    return "vehicles: array expected";
                for (let i = 0; i < message.vehicles.length; ++i) {
                    let error = $root.Vehicle.verify(message.vehicles[i]);
                    if (error)
                        return "vehicles." + error;
                }
            }
            if (message.roads != null && message.hasOwnProperty("roads")) {
                if (!Array.isArray(message.roads))
                    return "roads: array expected";
                for (let i = 0; i < message.roads.length; ++i) {
                    let error = $root.Road.verify(message.roads[i]);
                    if (error)
                        return "roads." + error;
                }
            }
            if (message.intersections != null && message.hasOwnProperty("intersections")) {
                if (!Array.isArray(message.intersections))
                    return "intersections: array expected";
                for (let i = 0; i < message.intersections.length; ++i) {
                    let error = $root.Intersection.verify(message.intersections[i]);
                    if (error)
                        return "intersections." + error;
                }
            }
            return null;
        };

        /**
         * Creates an Updated message from a plain object. Also converts values to their respective internal types.
         * @function fromObject
         * @memberof StateUpdate.Updated
         * @static
         * @param {Object.<string,*>} object Plain object
         * @returns {StateUpdate.Updated} Updated
         */
        Updated.fromObject = function fromObject(object) {
            if (object instanceof $root.StateUpdate.Updated)
                return object;
            let message = new $root.StateUpdate.Updated();
            if (object.vehicles) {
                if (!Array.isArray(object.vehicles))
                    throw TypeError(".StateUpdate.Updated.vehicles: array expected");
                message.vehicles = [];
                for (let i = 0; i < object.vehicles.length; ++i) {
                    if (typeof object.vehicles[i] !== "object")
                        throw TypeError(".StateUpdate.Updated.vehicles: object expected");
                    message.vehicles[i] = $root.Vehicle.fromObject(object.vehicles[i]);
                }
            }
            if (object.roads) {
                if (!Array.isArray(object.roads))
                    throw TypeError(".StateUpdate.Updated.roads: array expected");
                message.roads = [];
                for (let i = 0; i < object.roads.length; ++i) {
                    if (typeof object.roads[i] !== "object")
                        throw TypeError(".StateUpdate.Updated.roads: object expected");
                    message.roads[i] = $root.Road.fromObject(object.roads[i]);
                }
            }
            if (object.intersections) {
                if (!Array.isArray(object.intersections))
                    throw TypeError(".StateUpdate.Updated.intersections: array expected");
                message.intersections = [];
                for (let i = 0; i < object.intersections.length; ++i) {
                    if (typeof object.intersections[i] !== "object")
                        throw TypeError(".StateUpdate.Updated.intersections: object expected");
                    message.intersections[i] = $root.Intersection.fromObject(object.intersections[i]);
                }
            }
            return message;
        };

        /**
         * Creates a plain object from an Updated message. Also converts values to other types if specified.
         * @function toObject
         * @memberof StateUpdate.Updated
         * @static
         * @param {StateUpdate.Updated} message Updated
         * @param {$protobuf.IConversionOptions} [options] Conversion options
         * @returns {Object.<string,*>} Plain object
         */
        Updated.toObject = function toObject(message, options) {
            if (!options)
                options = {};
            let object = {};
            if (options.arrays || options.defaults) {
                object.vehicles = [];
                object.roads = [];
                object.intersections = [];
            }
            if (message.vehicles && message.vehicles.length) {
                object.vehicles = [];
                for (let j = 0; j < message.vehicles.length; ++j)
                    object.vehicles[j] = $root.Vehicle.toObject(message.vehicles[j], options);
            }
            if (message.roads && message.roads.length) {
                object.roads = [];
                for (let j = 0; j < message.roads.length; ++j)
                    object.roads[j] = $root.Road.toObject(message.roads[j], options);
            }
            if (message.intersections && message.intersections.length) {
                object.intersections = [];
                for (let j = 0; j < message.intersections.length; ++j)
                    object.intersections[j] = $root.Intersection.toObject(message.intersections[j], options);
            }
            return object;
        };

        /**
         * Converts this Updated to JSON.
         * @function toJSON
         * @memberof StateUpdate.Updated
         * @instance
         * @returns {Object.<string,*>} JSON object
         */
        Updated.prototype.toJSON = function toJSON() {
            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
        };

        return Updated;
    })();

    StateUpdate.Deleted = (function() {

        /**
         * Properties of a Deleted.
         * @memberof StateUpdate
         * @interface IDeleted
         * @property {Array.<string>|null} [vehicles] Deleted vehicles
         * @property {Array.<string>|null} [roads] Deleted roads
         * @property {Array.<string>|null} [intersections] Deleted intersections
         */

        /**
         * Constructs a new Deleted.
         * @memberof StateUpdate
         * @classdesc Represents a Deleted.
         * @implements IDeleted
         * @constructor
         * @param {StateUpdate.IDeleted=} [properties] Properties to set
         */
        function Deleted(properties) {
            this.vehicles = [];
            this.roads = [];
            this.intersections = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * Deleted vehicles.
         * @member {Array.<string>} vehicles
         * @memberof StateUpdate.Deleted
         * @instance
         */
        Deleted.prototype.vehicles = $util.emptyArray;

        /**
         * Deleted roads.
         * @member {Array.<string>} roads
         * @memberof StateUpdate.Deleted
         * @instance
         */
        Deleted.prototype.roads = $util.emptyArray;

        /**
         * Deleted intersections.
         * @member {Array.<string>} intersections
         * @memberof StateUpdate.Deleted
         * @instance
         */
        Deleted.prototype.intersections = $util.emptyArray;

        /**
         * Creates a new Deleted instance using the specified properties.
         * @function create
         * @memberof StateUpdate.Deleted
         * @static
         * @param {StateUpdate.IDeleted=} [properties] Properties to set
         * @returns {StateUpdate.Deleted} Deleted instance
         */
        Deleted.create = function create(properties) {
            return new Deleted(properties);
        };

        /**
         * Encodes the specified Deleted message. Does not implicitly {@link StateUpdate.Deleted.verify|verify} messages.
         * @function encode
         * @memberof StateUpdate.Deleted
         * @static
         * @param {StateUpdate.IDeleted} message Deleted message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Deleted.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.vehicles != null && message.vehicles.length)
                for (let i = 0; i < message.vehicles.length; ++i)
                    writer.uint32(/* id 3, wireType 2 =*/26).string(message.vehicles[i]);
            if (message.roads != null && message.roads.length)
                for (let i = 0; i < message.roads.length; ++i)
                    writer.uint32(/* id 4, wireType 2 =*/34).string(message.roads[i]);
            if (message.intersections != null && message.intersections.length)
                for (let i = 0; i < message.intersections.length; ++i)
                    writer.uint32(/* id 5, wireType 2 =*/42).string(message.intersections[i]);
            return writer;
        };

        /**
         * Encodes the specified Deleted message, length delimited. Does not implicitly {@link StateUpdate.Deleted.verify|verify} messages.
         * @function encodeDelimited
         * @memberof StateUpdate.Deleted
         * @static
         * @param {StateUpdate.IDeleted} message Deleted message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Deleted.encodeDelimited = function encodeDelimited(message, writer) {
            return this.encode(message, writer).ldelim();
        };

        /**
         * Decodes a Deleted message from the specified reader or buffer.
         * @function decode
         * @memberof StateUpdate.Deleted
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {StateUpdate.Deleted} Deleted
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Deleted.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.StateUpdate.Deleted();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 3:
                    if (!(message.vehicles && message.vehicles.length))
                        message.vehicles = [];
                    message.vehicles.push(reader.string());
                    break;
                case 4:
                    if (!(message.roads && message.roads.length))
                        message.roads = [];
                    message.roads.push(reader.string());
                    break;
                case 5:
                    if (!(message.intersections && message.intersections.length))
                        message.intersections = [];
                    message.intersections.push(reader.string());
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        /**
         * Decodes a Deleted message from the specified reader or buffer, length delimited.
         * @function decodeDelimited
         * @memberof StateUpdate.Deleted
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @returns {StateUpdate.Deleted} Deleted
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Deleted.decodeDelimited = function decodeDelimited(reader) {
            if (!(reader instanceof $Reader))
                reader = new $Reader(reader);
            return this.decode(reader, reader.uint32());
        };

        /**
         * Verifies a Deleted message.
         * @function verify
         * @memberof StateUpdate.Deleted
         * @static
         * @param {Object.<string,*>} message Plain object to verify
         * @returns {string|null} `null` if valid, otherwise the reason why it is not
         */
        Deleted.verify = function verify(message) {
            if (typeof message !== "object" || message === null)
                return "object expected";
            if (message.vehicles != null && message.hasOwnProperty("vehicles")) {
                if (!Array.isArray(message.vehicles))
                    return "vehicles: array expected";
                for (let i = 0; i < message.vehicles.length; ++i)
                    if (!$util.isString(message.vehicles[i]))
                        return "vehicles: string[] expected";
            }
            if (message.roads != null && message.hasOwnProperty("roads")) {
                if (!Array.isArray(message.roads))
                    return "roads: array expected";
                for (let i = 0; i < message.roads.length; ++i)
                    if (!$util.isString(message.roads[i]))
                        return "roads: string[] expected";
            }
            if (message.intersections != null && message.hasOwnProperty("intersections")) {
                if (!Array.isArray(message.intersections))
                    return "intersections: array expected";
                for (let i = 0; i < message.intersections.length; ++i)
                    if (!$util.isString(message.intersections[i]))
                        return "intersections: string[] expected";
            }
            return null;
        };

        /**
         * Creates a Deleted message from a plain object. Also converts values to their respective internal types.
         * @function fromObject
         * @memberof StateUpdate.Deleted
         * @static
         * @param {Object.<string,*>} object Plain object
         * @returns {StateUpdate.Deleted} Deleted
         */
        Deleted.fromObject = function fromObject(object) {
            if (object instanceof $root.StateUpdate.Deleted)
                return object;
            let message = new $root.StateUpdate.Deleted();
            if (object.vehicles) {
                if (!Array.isArray(object.vehicles))
                    throw TypeError(".StateUpdate.Deleted.vehicles: array expected");
                message.vehicles = [];
                for (let i = 0; i < object.vehicles.length; ++i)
                    message.vehicles[i] = String(object.vehicles[i]);
            }
            if (object.roads) {
                if (!Array.isArray(object.roads))
                    throw TypeError(".StateUpdate.Deleted.roads: array expected");
                message.roads = [];
                for (let i = 0; i < object.roads.length; ++i)
                    message.roads[i] = String(object.roads[i]);
            }
            if (object.intersections) {
                if (!Array.isArray(object.intersections))
                    throw TypeError(".StateUpdate.Deleted.intersections: array expected");
                message.intersections = [];
                for (let i = 0; i < object.intersections.length; ++i)
                    message.intersections[i] = String(object.intersections[i]);
            }
            return message;
        };

        /**
         * Creates a plain object from a Deleted message. Also converts values to other types if specified.
         * @function toObject
         * @memberof StateUpdate.Deleted
         * @static
         * @param {StateUpdate.Deleted} message Deleted
         * @param {$protobuf.IConversionOptions} [options] Conversion options
         * @returns {Object.<string,*>} Plain object
         */
        Deleted.toObject = function toObject(message, options) {
            if (!options)
                options = {};
            let object = {};
            if (options.arrays || options.defaults) {
                object.vehicles = [];
                object.roads = [];
                object.intersections = [];
            }
            if (message.vehicles && message.vehicles.length) {
                object.vehicles = [];
                for (let j = 0; j < message.vehicles.length; ++j)
                    object.vehicles[j] = message.vehicles[j];
            }
            if (message.roads && message.roads.length) {
                object.roads = [];
                for (let j = 0; j < message.roads.length; ++j)
                    object.roads[j] = message.roads[j];
            }
            if (message.intersections && message.intersections.length) {
                object.intersections = [];
                for (let j = 0; j < message.intersections.length; ++j)
                    object.intersections[j] = message.intersections[j];
            }
            return object;
        };

        /**
         * Converts this Deleted to JSON.
         * @function toJSON
         * @memberof StateUpdate.Deleted
         * @instance
         * @returns {Object.<string,*>} JSON object
         */
        Deleted.prototype.toJSON = function toJSON() {
            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
        };

        return Deleted;
    })();

    /**
     * UpdateType enum.
     * @name StateUpdate.UpdateType
     * @enum {string}
     * @property {number} Delta=0 Delta value
     * @property {number} Full=1 Full value
     */
    StateUpdate.UpdateType = (function() {
        const valuesById = {}, values = Object.create(valuesById);
        values[valuesById[0] = "Delta"] = 0;
        values[valuesById[1] = "Full"] = 1;
        return values;
    })();

    StateUpdate.UpdateMeta = (function() {

        /**
         * Properties of an UpdateMeta.
         * @memberof StateUpdate
         * @interface IUpdateMeta
         * @property {number|null} [updatesPerSecond] UpdateMeta updatesPerSecond
         */

        /**
         * Constructs a new UpdateMeta.
         * @memberof StateUpdate
         * @classdesc Represents an UpdateMeta.
         * @implements IUpdateMeta
         * @constructor
         * @param {StateUpdate.IUpdateMeta=} [properties] Properties to set
         */
        function UpdateMeta(properties) {
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * UpdateMeta updatesPerSecond.
         * @member {number} updatesPerSecond
         * @memberof StateUpdate.UpdateMeta
         * @instance
         */
        UpdateMeta.prototype.updatesPerSecond = 0;

        /**
         * Creates a new UpdateMeta instance using the specified properties.
         * @function create
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {StateUpdate.IUpdateMeta=} [properties] Properties to set
         * @returns {StateUpdate.UpdateMeta} UpdateMeta instance
         */
        UpdateMeta.create = function create(properties) {
            return new UpdateMeta(properties);
        };

        /**
         * Encodes the specified UpdateMeta message. Does not implicitly {@link StateUpdate.UpdateMeta.verify|verify} messages.
         * @function encode
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {StateUpdate.IUpdateMeta} message UpdateMeta message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        UpdateMeta.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.updatesPerSecond != null && message.hasOwnProperty("updatesPerSecond"))
                writer.uint32(/* id 1, wireType 0 =*/8).uint32(message.updatesPerSecond);
            return writer;
        };

        /**
         * Encodes the specified UpdateMeta message, length delimited. Does not implicitly {@link StateUpdate.UpdateMeta.verify|verify} messages.
         * @function encodeDelimited
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {StateUpdate.IUpdateMeta} message UpdateMeta message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        UpdateMeta.encodeDelimited = function encodeDelimited(message, writer) {
            return this.encode(message, writer).ldelim();
        };

        /**
         * Decodes an UpdateMeta message from the specified reader or buffer.
         * @function decode
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {StateUpdate.UpdateMeta} UpdateMeta
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        UpdateMeta.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.StateUpdate.UpdateMeta();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    message.updatesPerSecond = reader.uint32();
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        /**
         * Decodes an UpdateMeta message from the specified reader or buffer, length delimited.
         * @function decodeDelimited
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @returns {StateUpdate.UpdateMeta} UpdateMeta
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        UpdateMeta.decodeDelimited = function decodeDelimited(reader) {
            if (!(reader instanceof $Reader))
                reader = new $Reader(reader);
            return this.decode(reader, reader.uint32());
        };

        /**
         * Verifies an UpdateMeta message.
         * @function verify
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {Object.<string,*>} message Plain object to verify
         * @returns {string|null} `null` if valid, otherwise the reason why it is not
         */
        UpdateMeta.verify = function verify(message) {
            if (typeof message !== "object" || message === null)
                return "object expected";
            if (message.updatesPerSecond != null && message.hasOwnProperty("updatesPerSecond"))
                if (!$util.isInteger(message.updatesPerSecond))
                    return "updatesPerSecond: integer expected";
            return null;
        };

        /**
         * Creates an UpdateMeta message from a plain object. Also converts values to their respective internal types.
         * @function fromObject
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {Object.<string,*>} object Plain object
         * @returns {StateUpdate.UpdateMeta} UpdateMeta
         */
        UpdateMeta.fromObject = function fromObject(object) {
            if (object instanceof $root.StateUpdate.UpdateMeta)
                return object;
            let message = new $root.StateUpdate.UpdateMeta();
            if (object.updatesPerSecond != null)
                message.updatesPerSecond = object.updatesPerSecond >>> 0;
            return message;
        };

        /**
         * Creates a plain object from an UpdateMeta message. Also converts values to other types if specified.
         * @function toObject
         * @memberof StateUpdate.UpdateMeta
         * @static
         * @param {StateUpdate.UpdateMeta} message UpdateMeta
         * @param {$protobuf.IConversionOptions} [options] Conversion options
         * @returns {Object.<string,*>} Plain object
         */
        UpdateMeta.toObject = function toObject(message, options) {
            if (!options)
                options = {};
            let object = {};
            if (options.defaults)
                object.updatesPerSecond = 0;
            if (message.updatesPerSecond != null && message.hasOwnProperty("updatesPerSecond"))
                object.updatesPerSecond = message.updatesPerSecond;
            return object;
        };

        /**
         * Converts this UpdateMeta to JSON.
         * @function toJSON
         * @memberof StateUpdate.UpdateMeta
         * @instance
         * @returns {Object.<string,*>} JSON object
         */
        UpdateMeta.prototype.toJSON = function toJSON() {
            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
        };

        return UpdateMeta;
    })();

    return StateUpdate;
})();

export const Vehicle = $root.Vehicle = (() => {

    /**
     * Properties of a Vehicle.
     * @exports IVehicle
     * @interface IVehicle
     * @property {string|null} [id] Vehicle id
     * @property {IPosition|null} [currentPosition] Vehicle currentPosition
     * @property {IPosition|null} [targetPosition] Vehicle targetPosition
     * @property {number|null} [acceleration] Vehicle acceleration
     * @property {number|null} [speed] Vehicle speed
     */

    /**
     * Constructs a new Vehicle.
     * @exports Vehicle
     * @classdesc Represents a Vehicle.
     * @implements IVehicle
     * @constructor
     * @param {IVehicle=} [properties] Properties to set
     */
    function Vehicle(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Vehicle id.
     * @member {string} id
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.id = "";

    /**
     * Vehicle currentPosition.
     * @member {IPosition|null|undefined} currentPosition
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.currentPosition = null;

    /**
     * Vehicle targetPosition.
     * @member {IPosition|null|undefined} targetPosition
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.targetPosition = null;

    /**
     * Vehicle acceleration.
     * @member {number} acceleration
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.acceleration = 0;

    /**
     * Vehicle speed.
     * @member {number} speed
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.speed = 0;

    /**
     * Creates a new Vehicle instance using the specified properties.
     * @function create
     * @memberof Vehicle
     * @static
     * @param {IVehicle=} [properties] Properties to set
     * @returns {Vehicle} Vehicle instance
     */
    Vehicle.create = function create(properties) {
        return new Vehicle(properties);
    };

    /**
     * Encodes the specified Vehicle message. Does not implicitly {@link Vehicle.verify|verify} messages.
     * @function encode
     * @memberof Vehicle
     * @static
     * @param {IVehicle} message Vehicle message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Vehicle.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.id != null && message.hasOwnProperty("id"))
            writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
        if (message.currentPosition != null && message.hasOwnProperty("currentPosition"))
            $root.Position.encode(message.currentPosition, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        if (message.targetPosition != null && message.hasOwnProperty("targetPosition"))
            $root.Position.encode(message.targetPosition, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
        if (message.acceleration != null && message.hasOwnProperty("acceleration"))
            writer.uint32(/* id 4, wireType 5 =*/37).float(message.acceleration);
        if (message.speed != null && message.hasOwnProperty("speed"))
            writer.uint32(/* id 5, wireType 5 =*/45).float(message.speed);
        return writer;
    };

    /**
     * Encodes the specified Vehicle message, length delimited. Does not implicitly {@link Vehicle.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Vehicle
     * @static
     * @param {IVehicle} message Vehicle message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Vehicle.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Vehicle message from the specified reader or buffer.
     * @function decode
     * @memberof Vehicle
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Vehicle} Vehicle
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Vehicle.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Vehicle();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.id = reader.string();
                break;
            case 2:
                message.currentPosition = $root.Position.decode(reader, reader.uint32());
                break;
            case 3:
                message.targetPosition = $root.Position.decode(reader, reader.uint32());
                break;
            case 4:
                message.acceleration = reader.float();
                break;
            case 5:
                message.speed = reader.float();
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a Vehicle message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Vehicle
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Vehicle} Vehicle
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Vehicle.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Vehicle message.
     * @function verify
     * @memberof Vehicle
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Vehicle.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.id != null && message.hasOwnProperty("id"))
            if (!$util.isString(message.id))
                return "id: string expected";
        if (message.currentPosition != null && message.hasOwnProperty("currentPosition")) {
            let error = $root.Position.verify(message.currentPosition);
            if (error)
                return "currentPosition." + error;
        }
        if (message.targetPosition != null && message.hasOwnProperty("targetPosition")) {
            let error = $root.Position.verify(message.targetPosition);
            if (error)
                return "targetPosition." + error;
        }
        if (message.acceleration != null && message.hasOwnProperty("acceleration"))
            if (typeof message.acceleration !== "number")
                return "acceleration: number expected";
        if (message.speed != null && message.hasOwnProperty("speed"))
            if (typeof message.speed !== "number")
                return "speed: number expected";
        return null;
    };

    /**
     * Creates a Vehicle message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Vehicle
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Vehicle} Vehicle
     */
    Vehicle.fromObject = function fromObject(object) {
        if (object instanceof $root.Vehicle)
            return object;
        let message = new $root.Vehicle();
        if (object.id != null)
            message.id = String(object.id);
        if (object.currentPosition != null) {
            if (typeof object.currentPosition !== "object")
                throw TypeError(".Vehicle.currentPosition: object expected");
            message.currentPosition = $root.Position.fromObject(object.currentPosition);
        }
        if (object.targetPosition != null) {
            if (typeof object.targetPosition !== "object")
                throw TypeError(".Vehicle.targetPosition: object expected");
            message.targetPosition = $root.Position.fromObject(object.targetPosition);
        }
        if (object.acceleration != null)
            message.acceleration = Number(object.acceleration);
        if (object.speed != null)
            message.speed = Number(object.speed);
        return message;
    };

    /**
     * Creates a plain object from a Vehicle message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Vehicle
     * @static
     * @param {Vehicle} message Vehicle
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Vehicle.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults) {
            object.id = "";
            object.currentPosition = null;
            object.targetPosition = null;
            object.acceleration = 0;
            object.speed = 0;
        }
        if (message.id != null && message.hasOwnProperty("id"))
            object.id = message.id;
        if (message.currentPosition != null && message.hasOwnProperty("currentPosition"))
            object.currentPosition = $root.Position.toObject(message.currentPosition, options);
        if (message.targetPosition != null && message.hasOwnProperty("targetPosition"))
            object.targetPosition = $root.Position.toObject(message.targetPosition, options);
        if (message.acceleration != null && message.hasOwnProperty("acceleration"))
            object.acceleration = options.json && !isFinite(message.acceleration) ? String(message.acceleration) : message.acceleration;
        if (message.speed != null && message.hasOwnProperty("speed"))
            object.speed = options.json && !isFinite(message.speed) ? String(message.speed) : message.speed;
        return object;
    };

    /**
     * Converts this Vehicle to JSON.
     * @function toJSON
     * @memberof Vehicle
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Vehicle.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Vehicle;
})();

export const Road = $root.Road = (() => {

    /**
     * Properties of a Road.
     * @exports IRoad
     * @interface IRoad
     */

    /**
     * Constructs a new Road.
     * @exports Road
     * @classdesc Represents a Road.
     * @implements IRoad
     * @constructor
     * @param {IRoad=} [properties] Properties to set
     */
    function Road(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Creates a new Road instance using the specified properties.
     * @function create
     * @memberof Road
     * @static
     * @param {IRoad=} [properties] Properties to set
     * @returns {Road} Road instance
     */
    Road.create = function create(properties) {
        return new Road(properties);
    };

    /**
     * Encodes the specified Road message. Does not implicitly {@link Road.verify|verify} messages.
     * @function encode
     * @memberof Road
     * @static
     * @param {IRoad} message Road message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Road.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        return writer;
    };

    /**
     * Encodes the specified Road message, length delimited. Does not implicitly {@link Road.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Road
     * @static
     * @param {IRoad} message Road message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Road.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Road message from the specified reader or buffer.
     * @function decode
     * @memberof Road
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Road} Road
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Road.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Road();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a Road message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Road
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Road} Road
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Road.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Road message.
     * @function verify
     * @memberof Road
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Road.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        return null;
    };

    /**
     * Creates a Road message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Road
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Road} Road
     */
    Road.fromObject = function fromObject(object) {
        if (object instanceof $root.Road)
            return object;
        return new $root.Road();
    };

    /**
     * Creates a plain object from a Road message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Road
     * @static
     * @param {Road} message Road
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Road.toObject = function toObject() {
        return {};
    };

    /**
     * Converts this Road to JSON.
     * @function toJSON
     * @memberof Road
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Road.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Road;
})();

export const Intersection = $root.Intersection = (() => {

    /**
     * Properties of an Intersection.
     * @exports IIntersection
     * @interface IIntersection
     */

    /**
     * Constructs a new Intersection.
     * @exports Intersection
     * @classdesc Represents an Intersection.
     * @implements IIntersection
     * @constructor
     * @param {IIntersection=} [properties] Properties to set
     */
    function Intersection(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Creates a new Intersection instance using the specified properties.
     * @function create
     * @memberof Intersection
     * @static
     * @param {IIntersection=} [properties] Properties to set
     * @returns {Intersection} Intersection instance
     */
    Intersection.create = function create(properties) {
        return new Intersection(properties);
    };

    /**
     * Encodes the specified Intersection message. Does not implicitly {@link Intersection.verify|verify} messages.
     * @function encode
     * @memberof Intersection
     * @static
     * @param {IIntersection} message Intersection message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Intersection.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        return writer;
    };

    /**
     * Encodes the specified Intersection message, length delimited. Does not implicitly {@link Intersection.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Intersection
     * @static
     * @param {IIntersection} message Intersection message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Intersection.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes an Intersection message from the specified reader or buffer.
     * @function decode
     * @memberof Intersection
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Intersection} Intersection
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Intersection.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Intersection();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes an Intersection message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Intersection
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Intersection} Intersection
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Intersection.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies an Intersection message.
     * @function verify
     * @memberof Intersection
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Intersection.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        return null;
    };

    /**
     * Creates an Intersection message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Intersection
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Intersection} Intersection
     */
    Intersection.fromObject = function fromObject(object) {
        if (object instanceof $root.Intersection)
            return object;
        return new $root.Intersection();
    };

    /**
     * Creates a plain object from an Intersection message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Intersection
     * @static
     * @param {Intersection} message Intersection
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Intersection.toObject = function toObject() {
        return {};
    };

    /**
     * Converts this Intersection to JSON.
     * @function toJSON
     * @memberof Intersection
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Intersection.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Intersection;
})();

export { $root as default };

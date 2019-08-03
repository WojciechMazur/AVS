/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
import * as $protobuf from "protobufjs/minimal";

// Common aliases
const $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

// Exported root namespace
const $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

export const Vector3 = $root.Vector3 = (() => {

    /**
     * Properties of a Vector3.
     * @exports IVector3
     * @interface IVector3
     * @property {number|null} [x] Vector3 x
     * @property {number|null} [y] Vector3 y
     * @property {number|null} [z] Vector3 z
     */

    /**
     * Constructs a new Vector3.
     * @exports Vector3
     * @classdesc Represents a Vector3.
     * @implements IVector3
     * @constructor
     * @param {IVector3=} [properties] Properties to set
     */
    function Vector3(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Vector3 x.
     * @member {number} x
     * @memberof Vector3
     * @instance
     */
    Vector3.prototype.x = 0;

    /**
     * Vector3 y.
     * @member {number} y
     * @memberof Vector3
     * @instance
     */
    Vector3.prototype.y = 0;

    /**
     * Vector3 z.
     * @member {number} z
     * @memberof Vector3
     * @instance
     */
    Vector3.prototype.z = 0;

    /**
     * Creates a new Vector3 instance using the specified properties.
     * @function create
     * @memberof Vector3
     * @static
     * @param {IVector3=} [properties] Properties to set
     * @returns {Vector3} Vector3 instance
     */
    Vector3.create = function create(properties) {
        return new Vector3(properties);
    };

    /**
     * Encodes the specified Vector3 message. Does not implicitly {@link Vector3.verify|verify} messages.
     * @function encode
     * @memberof Vector3
     * @static
     * @param {IVector3} message Vector3 message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Vector3.encode = function encode(message, writer) {
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
     * Encodes the specified Vector3 message, length delimited. Does not implicitly {@link Vector3.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Vector3
     * @static
     * @param {IVector3} message Vector3 message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Vector3.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Vector3 message from the specified reader or buffer.
     * @function decode
     * @memberof Vector3
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Vector3} Vector3
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Vector3.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Vector3();
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
     * Decodes a Vector3 message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Vector3
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Vector3} Vector3
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Vector3.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Vector3 message.
     * @function verify
     * @memberof Vector3
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Vector3.verify = function verify(message) {
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
     * Creates a Vector3 message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Vector3
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Vector3} Vector3
     */
    Vector3.fromObject = function fromObject(object) {
        if (object instanceof $root.Vector3)
            return object;
        let message = new $root.Vector3();
        if (object.x != null)
            message.x = Number(object.x);
        if (object.y != null)
            message.y = Number(object.y);
        if (object.z != null)
            message.z = Number(object.z);
        return message;
    };

    /**
     * Creates a plain object from a Vector3 message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Vector3
     * @static
     * @param {Vector3} message Vector3
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Vector3.toObject = function toObject(message, options) {
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
     * Converts this Vector3 to JSON.
     * @function toJSON
     * @memberof Vector3
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Vector3.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Vector3;
})();

export const BoundingBox = $root.BoundingBox = (() => {

    /**
     * Properties of a BoundingBox.
     * @exports IBoundingBox
     * @interface IBoundingBox
     * @property {number|null} [minX] BoundingBox minX
     * @property {number|null} [maxX] BoundingBox maxX
     * @property {number|null} [minY] BoundingBox minY
     * @property {number|null} [maxY] BoundingBox maxY
     */

    /**
     * Constructs a new BoundingBox.
     * @exports BoundingBox
     * @classdesc Represents a BoundingBox.
     * @implements IBoundingBox
     * @constructor
     * @param {IBoundingBox=} [properties] Properties to set
     */
    function BoundingBox(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * BoundingBox minX.
     * @member {number} minX
     * @memberof BoundingBox
     * @instance
     */
    BoundingBox.prototype.minX = 0;

    /**
     * BoundingBox maxX.
     * @member {number} maxX
     * @memberof BoundingBox
     * @instance
     */
    BoundingBox.prototype.maxX = 0;

    /**
     * BoundingBox minY.
     * @member {number} minY
     * @memberof BoundingBox
     * @instance
     */
    BoundingBox.prototype.minY = 0;

    /**
     * BoundingBox maxY.
     * @member {number} maxY
     * @memberof BoundingBox
     * @instance
     */
    BoundingBox.prototype.maxY = 0;

    /**
     * Creates a new BoundingBox instance using the specified properties.
     * @function create
     * @memberof BoundingBox
     * @static
     * @param {IBoundingBox=} [properties] Properties to set
     * @returns {BoundingBox} BoundingBox instance
     */
    BoundingBox.create = function create(properties) {
        return new BoundingBox(properties);
    };

    /**
     * Encodes the specified BoundingBox message. Does not implicitly {@link BoundingBox.verify|verify} messages.
     * @function encode
     * @memberof BoundingBox
     * @static
     * @param {IBoundingBox} message BoundingBox message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    BoundingBox.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.minX != null && message.hasOwnProperty("minX"))
            writer.uint32(/* id 1, wireType 5 =*/13).float(message.minX);
        if (message.maxX != null && message.hasOwnProperty("maxX"))
            writer.uint32(/* id 2, wireType 5 =*/21).float(message.maxX);
        if (message.minY != null && message.hasOwnProperty("minY"))
            writer.uint32(/* id 3, wireType 5 =*/29).float(message.minY);
        if (message.maxY != null && message.hasOwnProperty("maxY"))
            writer.uint32(/* id 4, wireType 5 =*/37).float(message.maxY);
        return writer;
    };

    /**
     * Encodes the specified BoundingBox message, length delimited. Does not implicitly {@link BoundingBox.verify|verify} messages.
     * @function encodeDelimited
     * @memberof BoundingBox
     * @static
     * @param {IBoundingBox} message BoundingBox message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    BoundingBox.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a BoundingBox message from the specified reader or buffer.
     * @function decode
     * @memberof BoundingBox
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {BoundingBox} BoundingBox
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    BoundingBox.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.BoundingBox();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.minX = reader.float();
                break;
            case 2:
                message.maxX = reader.float();
                break;
            case 3:
                message.minY = reader.float();
                break;
            case 4:
                message.maxY = reader.float();
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a BoundingBox message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof BoundingBox
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {BoundingBox} BoundingBox
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    BoundingBox.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a BoundingBox message.
     * @function verify
     * @memberof BoundingBox
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    BoundingBox.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.minX != null && message.hasOwnProperty("minX"))
            if (typeof message.minX !== "number")
                return "minX: number expected";
        if (message.maxX != null && message.hasOwnProperty("maxX"))
            if (typeof message.maxX !== "number")
                return "maxX: number expected";
        if (message.minY != null && message.hasOwnProperty("minY"))
            if (typeof message.minY !== "number")
                return "minY: number expected";
        if (message.maxY != null && message.hasOwnProperty("maxY"))
            if (typeof message.maxY !== "number")
                return "maxY: number expected";
        return null;
    };

    /**
     * Creates a BoundingBox message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof BoundingBox
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {BoundingBox} BoundingBox
     */
    BoundingBox.fromObject = function fromObject(object) {
        if (object instanceof $root.BoundingBox)
            return object;
        let message = new $root.BoundingBox();
        if (object.minX != null)
            message.minX = Number(object.minX);
        if (object.maxX != null)
            message.maxX = Number(object.maxX);
        if (object.minY != null)
            message.minY = Number(object.minY);
        if (object.maxY != null)
            message.maxY = Number(object.maxY);
        return message;
    };

    /**
     * Creates a plain object from a BoundingBox message. Also converts values to other types if specified.
     * @function toObject
     * @memberof BoundingBox
     * @static
     * @param {BoundingBox} message BoundingBox
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    BoundingBox.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults) {
            object.minX = 0;
            object.maxX = 0;
            object.minY = 0;
            object.maxY = 0;
        }
        if (message.minX != null && message.hasOwnProperty("minX"))
            object.minX = options.json && !isFinite(message.minX) ? String(message.minX) : message.minX;
        if (message.maxX != null && message.hasOwnProperty("maxX"))
            object.maxX = options.json && !isFinite(message.maxX) ? String(message.maxX) : message.maxX;
        if (message.minY != null && message.hasOwnProperty("minY"))
            object.minY = options.json && !isFinite(message.minY) ? String(message.minY) : message.minY;
        if (message.maxY != null && message.hasOwnProperty("maxY"))
            object.maxY = options.json && !isFinite(message.maxY) ? String(message.maxY) : message.maxY;
        return object;
    };

    /**
     * Converts this BoundingBox to JSON.
     * @function toJSON
     * @memberof BoundingBox
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    BoundingBox.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return BoundingBox;
})();

export const Geometry = $root.Geometry = (() => {

    /**
     * Properties of a Geometry.
     * @exports IGeometry
     * @interface IGeometry
     * @property {IVector3|null} [position] Geometry position
     * @property {Array.<Geometry.IShape>|null} [shapes] Geometry shapes
     */

    /**
     * Constructs a new Geometry.
     * @exports Geometry
     * @classdesc Represents a Geometry.
     * @implements IGeometry
     * @constructor
     * @param {IGeometry=} [properties] Properties to set
     */
    function Geometry(properties) {
        this.shapes = [];
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Geometry position.
     * @member {IVector3|null|undefined} position
     * @memberof Geometry
     * @instance
     */
    Geometry.prototype.position = null;

    /**
     * Geometry shapes.
     * @member {Array.<Geometry.IShape>} shapes
     * @memberof Geometry
     * @instance
     */
    Geometry.prototype.shapes = $util.emptyArray;

    /**
     * Creates a new Geometry instance using the specified properties.
     * @function create
     * @memberof Geometry
     * @static
     * @param {IGeometry=} [properties] Properties to set
     * @returns {Geometry} Geometry instance
     */
    Geometry.create = function create(properties) {
        return new Geometry(properties);
    };

    /**
     * Encodes the specified Geometry message. Does not implicitly {@link Geometry.verify|verify} messages.
     * @function encode
     * @memberof Geometry
     * @static
     * @param {IGeometry} message Geometry message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Geometry.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.position != null && message.hasOwnProperty("position"))
            $root.Vector3.encode(message.position, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
        if (message.shapes != null && message.shapes.length)
            for (let i = 0; i < message.shapes.length; ++i)
                $root.Geometry.Shape.encode(message.shapes[i], writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified Geometry message, length delimited. Does not implicitly {@link Geometry.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Geometry
     * @static
     * @param {IGeometry} message Geometry message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Geometry.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Geometry message from the specified reader or buffer.
     * @function decode
     * @memberof Geometry
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Geometry} Geometry
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Geometry.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Geometry();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.position = $root.Vector3.decode(reader, reader.uint32());
                break;
            case 2:
                if (!(message.shapes && message.shapes.length))
                    message.shapes = [];
                message.shapes.push($root.Geometry.Shape.decode(reader, reader.uint32()));
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a Geometry message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Geometry
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Geometry} Geometry
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Geometry.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Geometry message.
     * @function verify
     * @memberof Geometry
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Geometry.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.position != null && message.hasOwnProperty("position")) {
            let error = $root.Vector3.verify(message.position);
            if (error)
                return "position." + error;
        }
        if (message.shapes != null && message.hasOwnProperty("shapes")) {
            if (!Array.isArray(message.shapes))
                return "shapes: array expected";
            for (let i = 0; i < message.shapes.length; ++i) {
                let error = $root.Geometry.Shape.verify(message.shapes[i]);
                if (error)
                    return "shapes." + error;
            }
        }
        return null;
    };

    /**
     * Creates a Geometry message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Geometry
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Geometry} Geometry
     */
    Geometry.fromObject = function fromObject(object) {
        if (object instanceof $root.Geometry)
            return object;
        let message = new $root.Geometry();
        if (object.position != null) {
            if (typeof object.position !== "object")
                throw TypeError(".Geometry.position: object expected");
            message.position = $root.Vector3.fromObject(object.position);
        }
        if (object.shapes) {
            if (!Array.isArray(object.shapes))
                throw TypeError(".Geometry.shapes: array expected");
            message.shapes = [];
            for (let i = 0; i < object.shapes.length; ++i) {
                if (typeof object.shapes[i] !== "object")
                    throw TypeError(".Geometry.shapes: object expected");
                message.shapes[i] = $root.Geometry.Shape.fromObject(object.shapes[i]);
            }
        }
        return message;
    };

    /**
     * Creates a plain object from a Geometry message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Geometry
     * @static
     * @param {Geometry} message Geometry
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Geometry.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.arrays || options.defaults)
            object.shapes = [];
        if (options.defaults)
            object.position = null;
        if (message.position != null && message.hasOwnProperty("position"))
            object.position = $root.Vector3.toObject(message.position, options);
        if (message.shapes && message.shapes.length) {
            object.shapes = [];
            for (let j = 0; j < message.shapes.length; ++j)
                object.shapes[j] = $root.Geometry.Shape.toObject(message.shapes[j], options);
        }
        return object;
    };

    /**
     * Converts this Geometry to JSON.
     * @function toJSON
     * @memberof Geometry
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Geometry.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    Geometry.Shape = (function() {

        /**
         * Properties of a Shape.
         * @memberof Geometry
         * @interface IShape
         * @property {Array.<IVector3>|null} [indices] Shape indices
         */

        /**
         * Constructs a new Shape.
         * @memberof Geometry
         * @classdesc Represents a Shape.
         * @implements IShape
         * @constructor
         * @param {Geometry.IShape=} [properties] Properties to set
         */
        function Shape(properties) {
            this.indices = [];
            if (properties)
                for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                    if (properties[keys[i]] != null)
                        this[keys[i]] = properties[keys[i]];
        }

        /**
         * Shape indices.
         * @member {Array.<IVector3>} indices
         * @memberof Geometry.Shape
         * @instance
         */
        Shape.prototype.indices = $util.emptyArray;

        /**
         * Creates a new Shape instance using the specified properties.
         * @function create
         * @memberof Geometry.Shape
         * @static
         * @param {Geometry.IShape=} [properties] Properties to set
         * @returns {Geometry.Shape} Shape instance
         */
        Shape.create = function create(properties) {
            return new Shape(properties);
        };

        /**
         * Encodes the specified Shape message. Does not implicitly {@link Geometry.Shape.verify|verify} messages.
         * @function encode
         * @memberof Geometry.Shape
         * @static
         * @param {Geometry.IShape} message Shape message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Shape.encode = function encode(message, writer) {
            if (!writer)
                writer = $Writer.create();
            if (message.indices != null && message.indices.length)
                for (let i = 0; i < message.indices.length; ++i)
                    $root.Vector3.encode(message.indices[i], writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
            return writer;
        };

        /**
         * Encodes the specified Shape message, length delimited. Does not implicitly {@link Geometry.Shape.verify|verify} messages.
         * @function encodeDelimited
         * @memberof Geometry.Shape
         * @static
         * @param {Geometry.IShape} message Shape message or plain object to encode
         * @param {$protobuf.Writer} [writer] Writer to encode to
         * @returns {$protobuf.Writer} Writer
         */
        Shape.encodeDelimited = function encodeDelimited(message, writer) {
            return this.encode(message, writer).ldelim();
        };

        /**
         * Decodes a Shape message from the specified reader or buffer.
         * @function decode
         * @memberof Geometry.Shape
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @param {number} [length] Message length if known beforehand
         * @returns {Geometry.Shape} Shape
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Shape.decode = function decode(reader, length) {
            if (!(reader instanceof $Reader))
                reader = $Reader.create(reader);
            let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Geometry.Shape();
            while (reader.pos < end) {
                let tag = reader.uint32();
                switch (tag >>> 3) {
                case 1:
                    if (!(message.indices && message.indices.length))
                        message.indices = [];
                    message.indices.push($root.Vector3.decode(reader, reader.uint32()));
                    break;
                default:
                    reader.skipType(tag & 7);
                    break;
                }
            }
            return message;
        };

        /**
         * Decodes a Shape message from the specified reader or buffer, length delimited.
         * @function decodeDelimited
         * @memberof Geometry.Shape
         * @static
         * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
         * @returns {Geometry.Shape} Shape
         * @throws {Error} If the payload is not a reader or valid buffer
         * @throws {$protobuf.util.ProtocolError} If required fields are missing
         */
        Shape.decodeDelimited = function decodeDelimited(reader) {
            if (!(reader instanceof $Reader))
                reader = new $Reader(reader);
            return this.decode(reader, reader.uint32());
        };

        /**
         * Verifies a Shape message.
         * @function verify
         * @memberof Geometry.Shape
         * @static
         * @param {Object.<string,*>} message Plain object to verify
         * @returns {string|null} `null` if valid, otherwise the reason why it is not
         */
        Shape.verify = function verify(message) {
            if (typeof message !== "object" || message === null)
                return "object expected";
            if (message.indices != null && message.hasOwnProperty("indices")) {
                if (!Array.isArray(message.indices))
                    return "indices: array expected";
                for (let i = 0; i < message.indices.length; ++i) {
                    let error = $root.Vector3.verify(message.indices[i]);
                    if (error)
                        return "indices." + error;
                }
            }
            return null;
        };

        /**
         * Creates a Shape message from a plain object. Also converts values to their respective internal types.
         * @function fromObject
         * @memberof Geometry.Shape
         * @static
         * @param {Object.<string,*>} object Plain object
         * @returns {Geometry.Shape} Shape
         */
        Shape.fromObject = function fromObject(object) {
            if (object instanceof $root.Geometry.Shape)
                return object;
            let message = new $root.Geometry.Shape();
            if (object.indices) {
                if (!Array.isArray(object.indices))
                    throw TypeError(".Geometry.Shape.indices: array expected");
                message.indices = [];
                for (let i = 0; i < object.indices.length; ++i) {
                    if (typeof object.indices[i] !== "object")
                        throw TypeError(".Geometry.Shape.indices: object expected");
                    message.indices[i] = $root.Vector3.fromObject(object.indices[i]);
                }
            }
            return message;
        };

        /**
         * Creates a plain object from a Shape message. Also converts values to other types if specified.
         * @function toObject
         * @memberof Geometry.Shape
         * @static
         * @param {Geometry.Shape} message Shape
         * @param {$protobuf.IConversionOptions} [options] Conversion options
         * @returns {Object.<string,*>} Plain object
         */
        Shape.toObject = function toObject(message, options) {
            if (!options)
                options = {};
            let object = {};
            if (options.arrays || options.defaults)
                object.indices = [];
            if (message.indices && message.indices.length) {
                object.indices = [];
                for (let j = 0; j < message.indices.length; ++j)
                    object.indices[j] = $root.Vector3.toObject(message.indices[j], options);
            }
            return object;
        };

        /**
         * Converts this Shape to JSON.
         * @function toJSON
         * @memberof Geometry.Shape
         * @instance
         * @returns {Object.<string,*>} JSON object
         */
        Shape.prototype.toJSON = function toJSON() {
            return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
        };

        return Shape;
    })();

    return Geometry;
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
     * @property {IVector3|null} [currentPosition] Vehicle currentPosition
     * @property {IVector3|null} [targetPosition] Vehicle targetPosition
     * @property {number|null} [heading] Vehicle heading
     * @property {number|null} [acceleration] Vehicle acceleration
     * @property {number|null} [speed] Vehicle speed
     * @property {IVehicleSpec|null} [spec] Vehicle spec
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
     * @member {IVector3|null|undefined} currentPosition
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.currentPosition = null;

    /**
     * Vehicle targetPosition.
     * @member {IVector3|null|undefined} targetPosition
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.targetPosition = null;

    /**
     * Vehicle heading.
     * @member {number} heading
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.heading = 0;

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
     * Vehicle spec.
     * @member {IVehicleSpec|null|undefined} spec
     * @memberof Vehicle
     * @instance
     */
    Vehicle.prototype.spec = null;

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
            $root.Vector3.encode(message.currentPosition, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        if (message.targetPosition != null && message.hasOwnProperty("targetPosition"))
            $root.Vector3.encode(message.targetPosition, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
        if (message.heading != null && message.hasOwnProperty("heading"))
            writer.uint32(/* id 4, wireType 5 =*/37).float(message.heading);
        if (message.acceleration != null && message.hasOwnProperty("acceleration"))
            writer.uint32(/* id 5, wireType 5 =*/45).float(message.acceleration);
        if (message.speed != null && message.hasOwnProperty("speed"))
            writer.uint32(/* id 6, wireType 5 =*/53).float(message.speed);
        if (message.spec != null && message.hasOwnProperty("spec"))
            $root.VehicleSpec.encode(message.spec, writer.uint32(/* id 7, wireType 2 =*/58).fork()).ldelim();
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
                message.currentPosition = $root.Vector3.decode(reader, reader.uint32());
                break;
            case 3:
                message.targetPosition = $root.Vector3.decode(reader, reader.uint32());
                break;
            case 4:
                message.heading = reader.float();
                break;
            case 5:
                message.acceleration = reader.float();
                break;
            case 6:
                message.speed = reader.float();
                break;
            case 7:
                message.spec = $root.VehicleSpec.decode(reader, reader.uint32());
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
            let error = $root.Vector3.verify(message.currentPosition);
            if (error)
                return "currentPosition." + error;
        }
        if (message.targetPosition != null && message.hasOwnProperty("targetPosition")) {
            let error = $root.Vector3.verify(message.targetPosition);
            if (error)
                return "targetPosition." + error;
        }
        if (message.heading != null && message.hasOwnProperty("heading"))
            if (typeof message.heading !== "number")
                return "heading: number expected";
        if (message.acceleration != null && message.hasOwnProperty("acceleration"))
            if (typeof message.acceleration !== "number")
                return "acceleration: number expected";
        if (message.speed != null && message.hasOwnProperty("speed"))
            if (typeof message.speed !== "number")
                return "speed: number expected";
        if (message.spec != null && message.hasOwnProperty("spec")) {
            let error = $root.VehicleSpec.verify(message.spec);
            if (error)
                return "spec." + error;
        }
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
            message.currentPosition = $root.Vector3.fromObject(object.currentPosition);
        }
        if (object.targetPosition != null) {
            if (typeof object.targetPosition !== "object")
                throw TypeError(".Vehicle.targetPosition: object expected");
            message.targetPosition = $root.Vector3.fromObject(object.targetPosition);
        }
        if (object.heading != null)
            message.heading = Number(object.heading);
        if (object.acceleration != null)
            message.acceleration = Number(object.acceleration);
        if (object.speed != null)
            message.speed = Number(object.speed);
        if (object.spec != null) {
            if (typeof object.spec !== "object")
                throw TypeError(".Vehicle.spec: object expected");
            message.spec = $root.VehicleSpec.fromObject(object.spec);
        }
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
            object.heading = 0;
            object.acceleration = 0;
            object.speed = 0;
            object.spec = null;
        }
        if (message.id != null && message.hasOwnProperty("id"))
            object.id = message.id;
        if (message.currentPosition != null && message.hasOwnProperty("currentPosition"))
            object.currentPosition = $root.Vector3.toObject(message.currentPosition, options);
        if (message.targetPosition != null && message.hasOwnProperty("targetPosition"))
            object.targetPosition = $root.Vector3.toObject(message.targetPosition, options);
        if (message.heading != null && message.hasOwnProperty("heading"))
            object.heading = options.json && !isFinite(message.heading) ? String(message.heading) : message.heading;
        if (message.acceleration != null && message.hasOwnProperty("acceleration"))
            object.acceleration = options.json && !isFinite(message.acceleration) ? String(message.acceleration) : message.acceleration;
        if (message.speed != null && message.hasOwnProperty("speed"))
            object.speed = options.json && !isFinite(message.speed) ? String(message.speed) : message.speed;
        if (message.spec != null && message.hasOwnProperty("spec"))
            object.spec = $root.VehicleSpec.toObject(message.spec, options);
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

export const VehicleSpec = $root.VehicleSpec = (() => {

    /**
     * Properties of a VehicleSpec.
     * @exports IVehicleSpec
     * @interface IVehicleSpec
     * @property {number|null} [width] VehicleSpec width
     * @property {number|null} [length] VehicleSpec length
     * @property {number|null} [height] VehicleSpec height
     * @property {IGeometry|null} [geometry] VehicleSpec geometry
     */

    /**
     * Constructs a new VehicleSpec.
     * @exports VehicleSpec
     * @classdesc Represents a VehicleSpec.
     * @implements IVehicleSpec
     * @constructor
     * @param {IVehicleSpec=} [properties] Properties to set
     */
    function VehicleSpec(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * VehicleSpec width.
     * @member {number} width
     * @memberof VehicleSpec
     * @instance
     */
    VehicleSpec.prototype.width = 0;

    /**
     * VehicleSpec length.
     * @member {number} length
     * @memberof VehicleSpec
     * @instance
     */
    VehicleSpec.prototype.length = 0;

    /**
     * VehicleSpec height.
     * @member {number} height
     * @memberof VehicleSpec
     * @instance
     */
    VehicleSpec.prototype.height = 0;

    /**
     * VehicleSpec geometry.
     * @member {IGeometry|null|undefined} geometry
     * @memberof VehicleSpec
     * @instance
     */
    VehicleSpec.prototype.geometry = null;

    /**
     * Creates a new VehicleSpec instance using the specified properties.
     * @function create
     * @memberof VehicleSpec
     * @static
     * @param {IVehicleSpec=} [properties] Properties to set
     * @returns {VehicleSpec} VehicleSpec instance
     */
    VehicleSpec.create = function create(properties) {
        return new VehicleSpec(properties);
    };

    /**
     * Encodes the specified VehicleSpec message. Does not implicitly {@link VehicleSpec.verify|verify} messages.
     * @function encode
     * @memberof VehicleSpec
     * @static
     * @param {IVehicleSpec} message VehicleSpec message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    VehicleSpec.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.width != null && message.hasOwnProperty("width"))
            writer.uint32(/* id 1, wireType 5 =*/13).float(message.width);
        if (message.length != null && message.hasOwnProperty("length"))
            writer.uint32(/* id 2, wireType 5 =*/21).float(message.length);
        if (message.height != null && message.hasOwnProperty("height"))
            writer.uint32(/* id 3, wireType 5 =*/29).float(message.height);
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            $root.Geometry.encode(message.geometry, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified VehicleSpec message, length delimited. Does not implicitly {@link VehicleSpec.verify|verify} messages.
     * @function encodeDelimited
     * @memberof VehicleSpec
     * @static
     * @param {IVehicleSpec} message VehicleSpec message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    VehicleSpec.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a VehicleSpec message from the specified reader or buffer.
     * @function decode
     * @memberof VehicleSpec
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {VehicleSpec} VehicleSpec
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    VehicleSpec.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.VehicleSpec();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.width = reader.float();
                break;
            case 2:
                message.length = reader.float();
                break;
            case 3:
                message.height = reader.float();
                break;
            case 4:
                message.geometry = $root.Geometry.decode(reader, reader.uint32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a VehicleSpec message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof VehicleSpec
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {VehicleSpec} VehicleSpec
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    VehicleSpec.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a VehicleSpec message.
     * @function verify
     * @memberof VehicleSpec
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    VehicleSpec.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.width != null && message.hasOwnProperty("width"))
            if (typeof message.width !== "number")
                return "width: number expected";
        if (message.length != null && message.hasOwnProperty("length"))
            if (typeof message.length !== "number")
                return "length: number expected";
        if (message.height != null && message.hasOwnProperty("height"))
            if (typeof message.height !== "number")
                return "height: number expected";
        if (message.geometry != null && message.hasOwnProperty("geometry")) {
            let error = $root.Geometry.verify(message.geometry);
            if (error)
                return "geometry." + error;
        }
        return null;
    };

    /**
     * Creates a VehicleSpec message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof VehicleSpec
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {VehicleSpec} VehicleSpec
     */
    VehicleSpec.fromObject = function fromObject(object) {
        if (object instanceof $root.VehicleSpec)
            return object;
        let message = new $root.VehicleSpec();
        if (object.width != null)
            message.width = Number(object.width);
        if (object.length != null)
            message.length = Number(object.length);
        if (object.height != null)
            message.height = Number(object.height);
        if (object.geometry != null) {
            if (typeof object.geometry !== "object")
                throw TypeError(".VehicleSpec.geometry: object expected");
            message.geometry = $root.Geometry.fromObject(object.geometry);
        }
        return message;
    };

    /**
     * Creates a plain object from a VehicleSpec message. Also converts values to other types if specified.
     * @function toObject
     * @memberof VehicleSpec
     * @static
     * @param {VehicleSpec} message VehicleSpec
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    VehicleSpec.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults) {
            object.width = 0;
            object.length = 0;
            object.height = 0;
            object.geometry = null;
        }
        if (message.width != null && message.hasOwnProperty("width"))
            object.width = options.json && !isFinite(message.width) ? String(message.width) : message.width;
        if (message.length != null && message.hasOwnProperty("length"))
            object.length = options.json && !isFinite(message.length) ? String(message.length) : message.length;
        if (message.height != null && message.hasOwnProperty("height"))
            object.height = options.json && !isFinite(message.height) ? String(message.height) : message.height;
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            object.geometry = $root.Geometry.toObject(message.geometry, options);
        return object;
    };

    /**
     * Converts this VehicleSpec to JSON.
     * @function toJSON
     * @memberof VehicleSpec
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    VehicleSpec.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return VehicleSpec;
})();

export const Road = $root.Road = (() => {

    /**
     * Properties of a Road.
     * @exports IRoad
     * @interface IRoad
     * @property {string|null} [id] Road id
     * @property {Array.<ILane>|null} [lanes] Road lanes
     * @property {IGeometry|null} [geometry] Road geometry
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
        this.lanes = [];
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Road id.
     * @member {string} id
     * @memberof Road
     * @instance
     */
    Road.prototype.id = "";

    /**
     * Road lanes.
     * @member {Array.<ILane>} lanes
     * @memberof Road
     * @instance
     */
    Road.prototype.lanes = $util.emptyArray;

    /**
     * Road geometry.
     * @member {IGeometry|null|undefined} geometry
     * @memberof Road
     * @instance
     */
    Road.prototype.geometry = null;

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
        if (message.id != null && message.hasOwnProperty("id"))
            writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
        if (message.lanes != null && message.lanes.length)
            for (let i = 0; i < message.lanes.length; ++i)
                $root.Lane.encode(message.lanes[i], writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            $root.Geometry.encode(message.geometry, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
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
            case 1:
                message.id = reader.string();
                break;
            case 2:
                if (!(message.lanes && message.lanes.length))
                    message.lanes = [];
                message.lanes.push($root.Lane.decode(reader, reader.uint32()));
                break;
            case 3:
                message.geometry = $root.Geometry.decode(reader, reader.uint32());
                break;
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
        if (message.id != null && message.hasOwnProperty("id"))
            if (!$util.isString(message.id))
                return "id: string expected";
        if (message.lanes != null && message.hasOwnProperty("lanes")) {
            if (!Array.isArray(message.lanes))
                return "lanes: array expected";
            for (let i = 0; i < message.lanes.length; ++i) {
                let error = $root.Lane.verify(message.lanes[i]);
                if (error)
                    return "lanes." + error;
            }
        }
        if (message.geometry != null && message.hasOwnProperty("geometry")) {
            let error = $root.Geometry.verify(message.geometry);
            if (error)
                return "geometry." + error;
        }
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
        let message = new $root.Road();
        if (object.id != null)
            message.id = String(object.id);
        if (object.lanes) {
            if (!Array.isArray(object.lanes))
                throw TypeError(".Road.lanes: array expected");
            message.lanes = [];
            for (let i = 0; i < object.lanes.length; ++i) {
                if (typeof object.lanes[i] !== "object")
                    throw TypeError(".Road.lanes: object expected");
                message.lanes[i] = $root.Lane.fromObject(object.lanes[i]);
            }
        }
        if (object.geometry != null) {
            if (typeof object.geometry !== "object")
                throw TypeError(".Road.geometry: object expected");
            message.geometry = $root.Geometry.fromObject(object.geometry);
        }
        return message;
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
    Road.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.arrays || options.defaults)
            object.lanes = [];
        if (options.defaults) {
            object.id = "";
            object.geometry = null;
        }
        if (message.id != null && message.hasOwnProperty("id"))
            object.id = message.id;
        if (message.lanes && message.lanes.length) {
            object.lanes = [];
            for (let j = 0; j < message.lanes.length; ++j)
                object.lanes[j] = $root.Lane.toObject(message.lanes[j], options);
        }
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            object.geometry = $root.Geometry.toObject(message.geometry, options);
        return object;
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

export const Lane = $root.Lane = (() => {

    /**
     * Properties of a Lane.
     * @exports ILane
     * @interface ILane
     * @property {string|null} [id] Lane id
     * @property {IGeometry|null} [geometry] Lane geometry
     * @property {IVector3|null} [entryPoint] Lane entryPoint
     * @property {IVector3|null} [exitPoint] Lane exitPoint
     * @property {ISpawnPoint|null} [spawnPoint] Lane spawnPoint
     * @property {ICollectPoint|null} [collectPoint] Lane collectPoint
     */

    /**
     * Constructs a new Lane.
     * @exports Lane
     * @classdesc Represents a Lane.
     * @implements ILane
     * @constructor
     * @param {ILane=} [properties] Properties to set
     */
    function Lane(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Lane id.
     * @member {string} id
     * @memberof Lane
     * @instance
     */
    Lane.prototype.id = "";

    /**
     * Lane geometry.
     * @member {IGeometry|null|undefined} geometry
     * @memberof Lane
     * @instance
     */
    Lane.prototype.geometry = null;

    /**
     * Lane entryPoint.
     * @member {IVector3|null|undefined} entryPoint
     * @memberof Lane
     * @instance
     */
    Lane.prototype.entryPoint = null;

    /**
     * Lane exitPoint.
     * @member {IVector3|null|undefined} exitPoint
     * @memberof Lane
     * @instance
     */
    Lane.prototype.exitPoint = null;

    /**
     * Lane spawnPoint.
     * @member {ISpawnPoint|null|undefined} spawnPoint
     * @memberof Lane
     * @instance
     */
    Lane.prototype.spawnPoint = null;

    /**
     * Lane collectPoint.
     * @member {ICollectPoint|null|undefined} collectPoint
     * @memberof Lane
     * @instance
     */
    Lane.prototype.collectPoint = null;

    /**
     * Creates a new Lane instance using the specified properties.
     * @function create
     * @memberof Lane
     * @static
     * @param {ILane=} [properties] Properties to set
     * @returns {Lane} Lane instance
     */
    Lane.create = function create(properties) {
        return new Lane(properties);
    };

    /**
     * Encodes the specified Lane message. Does not implicitly {@link Lane.verify|verify} messages.
     * @function encode
     * @memberof Lane
     * @static
     * @param {ILane} message Lane message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Lane.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.id != null && message.hasOwnProperty("id"))
            writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            $root.Geometry.encode(message.geometry, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        if (message.entryPoint != null && message.hasOwnProperty("entryPoint"))
            $root.Vector3.encode(message.entryPoint, writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
        if (message.exitPoint != null && message.hasOwnProperty("exitPoint"))
            $root.Vector3.encode(message.exitPoint, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
        if (message.spawnPoint != null && message.hasOwnProperty("spawnPoint"))
            $root.SpawnPoint.encode(message.spawnPoint, writer.uint32(/* id 5, wireType 2 =*/42).fork()).ldelim();
        if (message.collectPoint != null && message.hasOwnProperty("collectPoint"))
            $root.CollectPoint.encode(message.collectPoint, writer.uint32(/* id 6, wireType 2 =*/50).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified Lane message, length delimited. Does not implicitly {@link Lane.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Lane
     * @static
     * @param {ILane} message Lane message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Lane.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Lane message from the specified reader or buffer.
     * @function decode
     * @memberof Lane
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Lane} Lane
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Lane.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.Lane();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.id = reader.string();
                break;
            case 2:
                message.geometry = $root.Geometry.decode(reader, reader.uint32());
                break;
            case 3:
                message.entryPoint = $root.Vector3.decode(reader, reader.uint32());
                break;
            case 4:
                message.exitPoint = $root.Vector3.decode(reader, reader.uint32());
                break;
            case 5:
                message.spawnPoint = $root.SpawnPoint.decode(reader, reader.uint32());
                break;
            case 6:
                message.collectPoint = $root.CollectPoint.decode(reader, reader.uint32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a Lane message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Lane
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Lane} Lane
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Lane.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Lane message.
     * @function verify
     * @memberof Lane
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Lane.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.id != null && message.hasOwnProperty("id"))
            if (!$util.isString(message.id))
                return "id: string expected";
        if (message.geometry != null && message.hasOwnProperty("geometry")) {
            let error = $root.Geometry.verify(message.geometry);
            if (error)
                return "geometry." + error;
        }
        if (message.entryPoint != null && message.hasOwnProperty("entryPoint")) {
            let error = $root.Vector3.verify(message.entryPoint);
            if (error)
                return "entryPoint." + error;
        }
        if (message.exitPoint != null && message.hasOwnProperty("exitPoint")) {
            let error = $root.Vector3.verify(message.exitPoint);
            if (error)
                return "exitPoint." + error;
        }
        if (message.spawnPoint != null && message.hasOwnProperty("spawnPoint")) {
            let error = $root.SpawnPoint.verify(message.spawnPoint);
            if (error)
                return "spawnPoint." + error;
        }
        if (message.collectPoint != null && message.hasOwnProperty("collectPoint")) {
            let error = $root.CollectPoint.verify(message.collectPoint);
            if (error)
                return "collectPoint." + error;
        }
        return null;
    };

    /**
     * Creates a Lane message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Lane
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Lane} Lane
     */
    Lane.fromObject = function fromObject(object) {
        if (object instanceof $root.Lane)
            return object;
        let message = new $root.Lane();
        if (object.id != null)
            message.id = String(object.id);
        if (object.geometry != null) {
            if (typeof object.geometry !== "object")
                throw TypeError(".Lane.geometry: object expected");
            message.geometry = $root.Geometry.fromObject(object.geometry);
        }
        if (object.entryPoint != null) {
            if (typeof object.entryPoint !== "object")
                throw TypeError(".Lane.entryPoint: object expected");
            message.entryPoint = $root.Vector3.fromObject(object.entryPoint);
        }
        if (object.exitPoint != null) {
            if (typeof object.exitPoint !== "object")
                throw TypeError(".Lane.exitPoint: object expected");
            message.exitPoint = $root.Vector3.fromObject(object.exitPoint);
        }
        if (object.spawnPoint != null) {
            if (typeof object.spawnPoint !== "object")
                throw TypeError(".Lane.spawnPoint: object expected");
            message.spawnPoint = $root.SpawnPoint.fromObject(object.spawnPoint);
        }
        if (object.collectPoint != null) {
            if (typeof object.collectPoint !== "object")
                throw TypeError(".Lane.collectPoint: object expected");
            message.collectPoint = $root.CollectPoint.fromObject(object.collectPoint);
        }
        return message;
    };

    /**
     * Creates a plain object from a Lane message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Lane
     * @static
     * @param {Lane} message Lane
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Lane.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults) {
            object.id = "";
            object.geometry = null;
            object.entryPoint = null;
            object.exitPoint = null;
            object.spawnPoint = null;
            object.collectPoint = null;
        }
        if (message.id != null && message.hasOwnProperty("id"))
            object.id = message.id;
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            object.geometry = $root.Geometry.toObject(message.geometry, options);
        if (message.entryPoint != null && message.hasOwnProperty("entryPoint"))
            object.entryPoint = $root.Vector3.toObject(message.entryPoint, options);
        if (message.exitPoint != null && message.hasOwnProperty("exitPoint"))
            object.exitPoint = $root.Vector3.toObject(message.exitPoint, options);
        if (message.spawnPoint != null && message.hasOwnProperty("spawnPoint"))
            object.spawnPoint = $root.SpawnPoint.toObject(message.spawnPoint, options);
        if (message.collectPoint != null && message.hasOwnProperty("collectPoint"))
            object.collectPoint = $root.CollectPoint.toObject(message.collectPoint, options);
        return object;
    };

    /**
     * Converts this Lane to JSON.
     * @function toJSON
     * @memberof Lane
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Lane.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Lane;
})();

export const SpawnPoint = $root.SpawnPoint = (() => {

    /**
     * Properties of a SpawnPoint.
     * @exports ISpawnPoint
     * @interface ISpawnPoint
     * @property {IGeometry|null} [geometry] SpawnPoint geometry
     */

    /**
     * Constructs a new SpawnPoint.
     * @exports SpawnPoint
     * @classdesc Represents a SpawnPoint.
     * @implements ISpawnPoint
     * @constructor
     * @param {ISpawnPoint=} [properties] Properties to set
     */
    function SpawnPoint(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * SpawnPoint geometry.
     * @member {IGeometry|null|undefined} geometry
     * @memberof SpawnPoint
     * @instance
     */
    SpawnPoint.prototype.geometry = null;

    /**
     * Creates a new SpawnPoint instance using the specified properties.
     * @function create
     * @memberof SpawnPoint
     * @static
     * @param {ISpawnPoint=} [properties] Properties to set
     * @returns {SpawnPoint} SpawnPoint instance
     */
    SpawnPoint.create = function create(properties) {
        return new SpawnPoint(properties);
    };

    /**
     * Encodes the specified SpawnPoint message. Does not implicitly {@link SpawnPoint.verify|verify} messages.
     * @function encode
     * @memberof SpawnPoint
     * @static
     * @param {ISpawnPoint} message SpawnPoint message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    SpawnPoint.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            $root.Geometry.encode(message.geometry, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified SpawnPoint message, length delimited. Does not implicitly {@link SpawnPoint.verify|verify} messages.
     * @function encodeDelimited
     * @memberof SpawnPoint
     * @static
     * @param {ISpawnPoint} message SpawnPoint message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    SpawnPoint.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a SpawnPoint message from the specified reader or buffer.
     * @function decode
     * @memberof SpawnPoint
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {SpawnPoint} SpawnPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    SpawnPoint.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.SpawnPoint();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.geometry = $root.Geometry.decode(reader, reader.uint32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a SpawnPoint message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof SpawnPoint
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {SpawnPoint} SpawnPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    SpawnPoint.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a SpawnPoint message.
     * @function verify
     * @memberof SpawnPoint
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    SpawnPoint.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.geometry != null && message.hasOwnProperty("geometry")) {
            let error = $root.Geometry.verify(message.geometry);
            if (error)
                return "geometry." + error;
        }
        return null;
    };

    /**
     * Creates a SpawnPoint message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof SpawnPoint
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {SpawnPoint} SpawnPoint
     */
    SpawnPoint.fromObject = function fromObject(object) {
        if (object instanceof $root.SpawnPoint)
            return object;
        let message = new $root.SpawnPoint();
        if (object.geometry != null) {
            if (typeof object.geometry !== "object")
                throw TypeError(".SpawnPoint.geometry: object expected");
            message.geometry = $root.Geometry.fromObject(object.geometry);
        }
        return message;
    };

    /**
     * Creates a plain object from a SpawnPoint message. Also converts values to other types if specified.
     * @function toObject
     * @memberof SpawnPoint
     * @static
     * @param {SpawnPoint} message SpawnPoint
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    SpawnPoint.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults)
            object.geometry = null;
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            object.geometry = $root.Geometry.toObject(message.geometry, options);
        return object;
    };

    /**
     * Converts this SpawnPoint to JSON.
     * @function toJSON
     * @memberof SpawnPoint
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    SpawnPoint.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return SpawnPoint;
})();

export const CollectPoint = $root.CollectPoint = (() => {

    /**
     * Properties of a CollectPoint.
     * @exports ICollectPoint
     * @interface ICollectPoint
     * @property {IGeometry|null} [geometry] CollectPoint geometry
     */

    /**
     * Constructs a new CollectPoint.
     * @exports CollectPoint
     * @classdesc Represents a CollectPoint.
     * @implements ICollectPoint
     * @constructor
     * @param {ICollectPoint=} [properties] Properties to set
     */
    function CollectPoint(properties) {
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * CollectPoint geometry.
     * @member {IGeometry|null|undefined} geometry
     * @memberof CollectPoint
     * @instance
     */
    CollectPoint.prototype.geometry = null;

    /**
     * Creates a new CollectPoint instance using the specified properties.
     * @function create
     * @memberof CollectPoint
     * @static
     * @param {ICollectPoint=} [properties] Properties to set
     * @returns {CollectPoint} CollectPoint instance
     */
    CollectPoint.create = function create(properties) {
        return new CollectPoint(properties);
    };

    /**
     * Encodes the specified CollectPoint message. Does not implicitly {@link CollectPoint.verify|verify} messages.
     * @function encode
     * @memberof CollectPoint
     * @static
     * @param {ICollectPoint} message CollectPoint message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    CollectPoint.encode = function encode(message, writer) {
        if (!writer)
            writer = $Writer.create();
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            $root.Geometry.encode(message.geometry, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
        return writer;
    };

    /**
     * Encodes the specified CollectPoint message, length delimited. Does not implicitly {@link CollectPoint.verify|verify} messages.
     * @function encodeDelimited
     * @memberof CollectPoint
     * @static
     * @param {ICollectPoint} message CollectPoint message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    CollectPoint.encodeDelimited = function encodeDelimited(message, writer) {
        return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a CollectPoint message from the specified reader or buffer.
     * @function decode
     * @memberof CollectPoint
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {CollectPoint} CollectPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    CollectPoint.decode = function decode(reader, length) {
        if (!(reader instanceof $Reader))
            reader = $Reader.create(reader);
        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.CollectPoint();
        while (reader.pos < end) {
            let tag = reader.uint32();
            switch (tag >>> 3) {
            case 1:
                message.geometry = $root.Geometry.decode(reader, reader.uint32());
                break;
            default:
                reader.skipType(tag & 7);
                break;
            }
        }
        return message;
    };

    /**
     * Decodes a CollectPoint message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof CollectPoint
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {CollectPoint} CollectPoint
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    CollectPoint.decodeDelimited = function decodeDelimited(reader) {
        if (!(reader instanceof $Reader))
            reader = new $Reader(reader);
        return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a CollectPoint message.
     * @function verify
     * @memberof CollectPoint
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    CollectPoint.verify = function verify(message) {
        if (typeof message !== "object" || message === null)
            return "object expected";
        if (message.geometry != null && message.hasOwnProperty("geometry")) {
            let error = $root.Geometry.verify(message.geometry);
            if (error)
                return "geometry." + error;
        }
        return null;
    };

    /**
     * Creates a CollectPoint message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof CollectPoint
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {CollectPoint} CollectPoint
     */
    CollectPoint.fromObject = function fromObject(object) {
        if (object instanceof $root.CollectPoint)
            return object;
        let message = new $root.CollectPoint();
        if (object.geometry != null) {
            if (typeof object.geometry !== "object")
                throw TypeError(".CollectPoint.geometry: object expected");
            message.geometry = $root.Geometry.fromObject(object.geometry);
        }
        return message;
    };

    /**
     * Creates a plain object from a CollectPoint message. Also converts values to other types if specified.
     * @function toObject
     * @memberof CollectPoint
     * @static
     * @param {CollectPoint} message CollectPoint
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    CollectPoint.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.defaults)
            object.geometry = null;
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            object.geometry = $root.Geometry.toObject(message.geometry, options);
        return object;
    };

    /**
     * Converts this CollectPoint to JSON.
     * @function toJSON
     * @memberof CollectPoint
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    CollectPoint.prototype.toJSON = function toJSON() {
        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return CollectPoint;
})();

export const Intersection = $root.Intersection = (() => {

    /**
     * Properties of an Intersection.
     * @exports IIntersection
     * @interface IIntersection
     * @property {string|null} [id] Intersection id
     * @property {IGeometry|null} [geometry] Intersection geometry
     * @property {Array.<IVector3>|null} [entryPoints] Intersection entryPoints
     * @property {Array.<IVector3>|null} [exitPoints] Intersection exitPoints
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
        this.entryPoints = [];
        this.exitPoints = [];
        if (properties)
            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                if (properties[keys[i]] != null)
                    this[keys[i]] = properties[keys[i]];
    }

    /**
     * Intersection id.
     * @member {string} id
     * @memberof Intersection
     * @instance
     */
    Intersection.prototype.id = "";

    /**
     * Intersection geometry.
     * @member {IGeometry|null|undefined} geometry
     * @memberof Intersection
     * @instance
     */
    Intersection.prototype.geometry = null;

    /**
     * Intersection entryPoints.
     * @member {Array.<IVector3>} entryPoints
     * @memberof Intersection
     * @instance
     */
    Intersection.prototype.entryPoints = $util.emptyArray;

    /**
     * Intersection exitPoints.
     * @member {Array.<IVector3>} exitPoints
     * @memberof Intersection
     * @instance
     */
    Intersection.prototype.exitPoints = $util.emptyArray;

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
        if (message.id != null && message.hasOwnProperty("id"))
            writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            $root.Geometry.encode(message.geometry, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
        if (message.entryPoints != null && message.entryPoints.length)
            for (let i = 0; i < message.entryPoints.length; ++i)
                $root.Vector3.encode(message.entryPoints[i], writer.uint32(/* id 3, wireType 2 =*/26).fork()).ldelim();
        if (message.exitPoints != null && message.exitPoints.length)
            for (let i = 0; i < message.exitPoints.length; ++i)
                $root.Vector3.encode(message.exitPoints[i], writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
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
            case 1:
                message.id = reader.string();
                break;
            case 2:
                message.geometry = $root.Geometry.decode(reader, reader.uint32());
                break;
            case 3:
                if (!(message.entryPoints && message.entryPoints.length))
                    message.entryPoints = [];
                message.entryPoints.push($root.Vector3.decode(reader, reader.uint32()));
                break;
            case 4:
                if (!(message.exitPoints && message.exitPoints.length))
                    message.exitPoints = [];
                message.exitPoints.push($root.Vector3.decode(reader, reader.uint32()));
                break;
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
        if (message.id != null && message.hasOwnProperty("id"))
            if (!$util.isString(message.id))
                return "id: string expected";
        if (message.geometry != null && message.hasOwnProperty("geometry")) {
            let error = $root.Geometry.verify(message.geometry);
            if (error)
                return "geometry." + error;
        }
        if (message.entryPoints != null && message.hasOwnProperty("entryPoints")) {
            if (!Array.isArray(message.entryPoints))
                return "entryPoints: array expected";
            for (let i = 0; i < message.entryPoints.length; ++i) {
                let error = $root.Vector3.verify(message.entryPoints[i]);
                if (error)
                    return "entryPoints." + error;
            }
        }
        if (message.exitPoints != null && message.hasOwnProperty("exitPoints")) {
            if (!Array.isArray(message.exitPoints))
                return "exitPoints: array expected";
            for (let i = 0; i < message.exitPoints.length; ++i) {
                let error = $root.Vector3.verify(message.exitPoints[i]);
                if (error)
                    return "exitPoints." + error;
            }
        }
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
        let message = new $root.Intersection();
        if (object.id != null)
            message.id = String(object.id);
        if (object.geometry != null) {
            if (typeof object.geometry !== "object")
                throw TypeError(".Intersection.geometry: object expected");
            message.geometry = $root.Geometry.fromObject(object.geometry);
        }
        if (object.entryPoints) {
            if (!Array.isArray(object.entryPoints))
                throw TypeError(".Intersection.entryPoints: array expected");
            message.entryPoints = [];
            for (let i = 0; i < object.entryPoints.length; ++i) {
                if (typeof object.entryPoints[i] !== "object")
                    throw TypeError(".Intersection.entryPoints: object expected");
                message.entryPoints[i] = $root.Vector3.fromObject(object.entryPoints[i]);
            }
        }
        if (object.exitPoints) {
            if (!Array.isArray(object.exitPoints))
                throw TypeError(".Intersection.exitPoints: array expected");
            message.exitPoints = [];
            for (let i = 0; i < object.exitPoints.length; ++i) {
                if (typeof object.exitPoints[i] !== "object")
                    throw TypeError(".Intersection.exitPoints: object expected");
                message.exitPoints[i] = $root.Vector3.fromObject(object.exitPoints[i]);
            }
        }
        return message;
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
    Intersection.toObject = function toObject(message, options) {
        if (!options)
            options = {};
        let object = {};
        if (options.arrays || options.defaults) {
            object.entryPoints = [];
            object.exitPoints = [];
        }
        if (options.defaults) {
            object.id = "";
            object.geometry = null;
        }
        if (message.id != null && message.hasOwnProperty("id"))
            object.id = message.id;
        if (message.geometry != null && message.hasOwnProperty("geometry"))
            object.geometry = $root.Geometry.toObject(message.geometry, options);
        if (message.entryPoints && message.entryPoints.length) {
            object.entryPoints = [];
            for (let j = 0; j < message.entryPoints.length; ++j)
                object.entryPoints[j] = $root.Vector3.toObject(message.entryPoints[j], options);
        }
        if (message.exitPoints && message.exitPoints.length) {
            object.exitPoints = [];
            for (let j = 0; j < message.exitPoints.length; ++j)
                object.exitPoints[j] = $root.Vector3.toObject(message.exitPoints[j], options);
        }
        return object;
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

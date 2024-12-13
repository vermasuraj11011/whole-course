// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package proto.SensorMetric

@SerialVersionUID(0L)
final case class SensorMetric(
    sensorId: _root_.scala.Predef.String = "",
    avgTemperature: _root_.scala.Float = 0.0f,
    avgHumidity: _root_.scala.Float = 0.0f,
    minTemperature: _root_.scala.Float = 0.0f,
    maxTemperature: _root_.scala.Float = 0.0f,
    minHumidity: _root_.scala.Float = 0.0f,
    maxHumidity: _root_.scala.Float = 0.0f,
    weight: _root_.scala.Int = 0,
    unknownFields: _root_.scalapb.UnknownFieldSet = _root_.scalapb.UnknownFieldSet.empty
    ) extends scalapb.GeneratedMessage with scalapb.lenses.Updatable[SensorMetric] {
    @transient
    private[this] var __serializedSizeMemoized: _root_.scala.Int = 0
    private[this] def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      
      {
        val __value = sensorId
        if (!__value.isEmpty) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, __value)
        }
      };
      
      {
        val __value = avgTemperature
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(2, __value)
        }
      };
      
      {
        val __value = avgHumidity
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(3, __value)
        }
      };
      
      {
        val __value = minTemperature
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(4, __value)
        }
      };
      
      {
        val __value = maxTemperature
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(5, __value)
        }
      };
      
      {
        val __value = minHumidity
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(6, __value)
        }
      };
      
      {
        val __value = maxHumidity
        if (__value != 0.0f) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeFloatSize(7, __value)
        }
      };
      
      {
        val __value = weight
        if (__value != 0) {
          __size += _root_.com.google.protobuf.CodedOutputStream.computeInt32Size(8, __value)
        }
      };
      __size += unknownFields.serializedSize
      __size
    }
    override def serializedSize: _root_.scala.Int = {
      var __size = __serializedSizeMemoized
      if (__size == 0) {
        __size = __computeSerializedSize() + 1
        __serializedSizeMemoized = __size
      }
      __size - 1
      
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): _root_.scala.Unit = {
      {
        val __v = sensorId
        if (!__v.isEmpty) {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = avgTemperature
        if (__v != 0.0f) {
          _output__.writeFloat(2, __v)
        }
      };
      {
        val __v = avgHumidity
        if (__v != 0.0f) {
          _output__.writeFloat(3, __v)
        }
      };
      {
        val __v = minTemperature
        if (__v != 0.0f) {
          _output__.writeFloat(4, __v)
        }
      };
      {
        val __v = maxTemperature
        if (__v != 0.0f) {
          _output__.writeFloat(5, __v)
        }
      };
      {
        val __v = minHumidity
        if (__v != 0.0f) {
          _output__.writeFloat(6, __v)
        }
      };
      {
        val __v = maxHumidity
        if (__v != 0.0f) {
          _output__.writeFloat(7, __v)
        }
      };
      {
        val __v = weight
        if (__v != 0) {
          _output__.writeInt32(8, __v)
        }
      };
      unknownFields.writeTo(_output__)
    }
    def withSensorId(__v: _root_.scala.Predef.String): SensorMetric = copy(sensorId = __v)
    def withAvgTemperature(__v: _root_.scala.Float): SensorMetric = copy(avgTemperature = __v)
    def withAvgHumidity(__v: _root_.scala.Float): SensorMetric = copy(avgHumidity = __v)
    def withMinTemperature(__v: _root_.scala.Float): SensorMetric = copy(minTemperature = __v)
    def withMaxTemperature(__v: _root_.scala.Float): SensorMetric = copy(maxTemperature = __v)
    def withMinHumidity(__v: _root_.scala.Float): SensorMetric = copy(minHumidity = __v)
    def withMaxHumidity(__v: _root_.scala.Float): SensorMetric = copy(maxHumidity = __v)
    def withWeight(__v: _root_.scala.Int): SensorMetric = copy(weight = __v)
    def withUnknownFields(__v: _root_.scalapb.UnknownFieldSet) = copy(unknownFields = __v)
    def discardUnknownFields = copy(unknownFields = _root_.scalapb.UnknownFieldSet.empty)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): _root_.scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = sensorId
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = avgTemperature
          if (__t != 0.0f) __t else null
        }
        case 3 => {
          val __t = avgHumidity
          if (__t != 0.0f) __t else null
        }
        case 4 => {
          val __t = minTemperature
          if (__t != 0.0f) __t else null
        }
        case 5 => {
          val __t = maxTemperature
          if (__t != 0.0f) __t else null
        }
        case 6 => {
          val __t = minHumidity
          if (__t != 0.0f) __t else null
        }
        case 7 => {
          val __t = maxHumidity
          if (__t != 0.0f) __t else null
        }
        case 8 => {
          val __t = weight
          if (__t != 0) __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      _root_.scala.Predef.require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(sensorId)
        case 2 => _root_.scalapb.descriptors.PFloat(avgTemperature)
        case 3 => _root_.scalapb.descriptors.PFloat(avgHumidity)
        case 4 => _root_.scalapb.descriptors.PFloat(minTemperature)
        case 5 => _root_.scalapb.descriptors.PFloat(maxTemperature)
        case 6 => _root_.scalapb.descriptors.PFloat(minHumidity)
        case 7 => _root_.scalapb.descriptors.PFloat(maxHumidity)
        case 8 => _root_.scalapb.descriptors.PInt(weight)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion: proto.SensorMetric.SensorMetric.type = proto.SensorMetric.SensorMetric
    // @@protoc_insertion_point(GeneratedMessage[proto.SensorMetric])
}

object SensorMetric extends scalapb.GeneratedMessageCompanion[proto.SensorMetric.SensorMetric] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[proto.SensorMetric.SensorMetric] = this
  def parseFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): proto.SensorMetric.SensorMetric = {
    var __sensorId: _root_.scala.Predef.String = ""
    var __avgTemperature: _root_.scala.Float = 0.0f
    var __avgHumidity: _root_.scala.Float = 0.0f
    var __minTemperature: _root_.scala.Float = 0.0f
    var __maxTemperature: _root_.scala.Float = 0.0f
    var __minHumidity: _root_.scala.Float = 0.0f
    var __maxHumidity: _root_.scala.Float = 0.0f
    var __weight: _root_.scala.Int = 0
    var `_unknownFields__`: _root_.scalapb.UnknownFieldSet.Builder = null
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __sensorId = _input__.readStringRequireUtf8()
        case 21 =>
          __avgTemperature = _input__.readFloat()
        case 29 =>
          __avgHumidity = _input__.readFloat()
        case 37 =>
          __minTemperature = _input__.readFloat()
        case 45 =>
          __maxTemperature = _input__.readFloat()
        case 53 =>
          __minHumidity = _input__.readFloat()
        case 61 =>
          __maxHumidity = _input__.readFloat()
        case 64 =>
          __weight = _input__.readInt32()
        case tag =>
          if (_unknownFields__ == null) {
            _unknownFields__ = new _root_.scalapb.UnknownFieldSet.Builder()
          }
          _unknownFields__.parseField(tag, _input__)
      }
    }
    proto.SensorMetric.SensorMetric(
        sensorId = __sensorId,
        avgTemperature = __avgTemperature,
        avgHumidity = __avgHumidity,
        minTemperature = __minTemperature,
        maxTemperature = __maxTemperature,
        minHumidity = __minHumidity,
        maxHumidity = __maxHumidity,
        weight = __weight,
        unknownFields = if (_unknownFields__ == null) _root_.scalapb.UnknownFieldSet.empty else _unknownFields__.result()
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[proto.SensorMetric.SensorMetric] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      _root_.scala.Predef.require(__fieldsMap.keys.forall(_.containingMessage eq scalaDescriptor), "FieldDescriptor does not match message type.")
      proto.SensorMetric.SensorMetric(
        sensorId = __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        avgTemperature = __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        avgHumidity = __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        minTemperature = __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        maxTemperature = __fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        minHumidity = __fieldsMap.get(scalaDescriptor.findFieldByNumber(6).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        maxHumidity = __fieldsMap.get(scalaDescriptor.findFieldByNumber(7).get).map(_.as[_root_.scala.Float]).getOrElse(0.0f),
        weight = __fieldsMap.get(scalaDescriptor.findFieldByNumber(8).get).map(_.as[_root_.scala.Int]).getOrElse(0)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = SensorMetricProto.javaDescriptor.getMessageTypes().get(0)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = SensorMetricProto.scalaDescriptor.messages(0)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_ <: _root_.scalapb.GeneratedMessage]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = proto.SensorMetric.SensorMetric(
    sensorId = "",
    avgTemperature = 0.0f,
    avgHumidity = 0.0f,
    minTemperature = 0.0f,
    maxTemperature = 0.0f,
    minHumidity = 0.0f,
    maxHumidity = 0.0f,
    weight = 0
  )
  implicit class SensorMetricLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, proto.SensorMetric.SensorMetric]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, proto.SensorMetric.SensorMetric](_l) {
    def sensorId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.sensorId)((c_, f_) => c_.copy(sensorId = f_))
    def avgTemperature: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.avgTemperature)((c_, f_) => c_.copy(avgTemperature = f_))
    def avgHumidity: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.avgHumidity)((c_, f_) => c_.copy(avgHumidity = f_))
    def minTemperature: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.minTemperature)((c_, f_) => c_.copy(minTemperature = f_))
    def maxTemperature: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.maxTemperature)((c_, f_) => c_.copy(maxTemperature = f_))
    def minHumidity: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.minHumidity)((c_, f_) => c_.copy(minHumidity = f_))
    def maxHumidity: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Float] = field(_.maxHumidity)((c_, f_) => c_.copy(maxHumidity = f_))
    def weight: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Int] = field(_.weight)((c_, f_) => c_.copy(weight = f_))
  }
  final val SENSORID_FIELD_NUMBER = 1
  final val AVGTEMPERATURE_FIELD_NUMBER = 2
  final val AVGHUMIDITY_FIELD_NUMBER = 3
  final val MINTEMPERATURE_FIELD_NUMBER = 4
  final val MAXTEMPERATURE_FIELD_NUMBER = 5
  final val MINHUMIDITY_FIELD_NUMBER = 6
  final val MAXHUMIDITY_FIELD_NUMBER = 7
  final val WEIGHT_FIELD_NUMBER = 8
  def of(
    sensorId: _root_.scala.Predef.String,
    avgTemperature: _root_.scala.Float,
    avgHumidity: _root_.scala.Float,
    minTemperature: _root_.scala.Float,
    maxTemperature: _root_.scala.Float,
    minHumidity: _root_.scala.Float,
    maxHumidity: _root_.scala.Float,
    weight: _root_.scala.Int
  ): _root_.proto.SensorMetric.SensorMetric = _root_.proto.SensorMetric.SensorMetric(
    sensorId,
    avgTemperature,
    avgHumidity,
    minTemperature,
    maxTemperature,
    minHumidity,
    maxHumidity,
    weight
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[proto.SensorMetric])
}
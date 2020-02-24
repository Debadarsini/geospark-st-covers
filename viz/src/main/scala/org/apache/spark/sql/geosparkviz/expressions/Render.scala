/*
 * FILE: Render.scala
 * Copyright (c) 2015 - 2019 GeoSpark Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.sql.geosparkviz.expressions

import java.awt.image.BufferedImage

import org.apache.spark.internal.Logging
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.geosparkviz.UDT.{ImageWrapperUDT, PixelUDT}
import org.apache.spark.sql.types._
import org.datasyslab.geosparkviz.core.ImageSerializableWrapper
import org.datasyslab.geosparkviz.utils.Pixel

case class ST_Render() extends UserDefinedAggregateFunction with Logging{
  // This is the input fields for your aggregate function.
  override def inputSchema: org.apache.spark.sql.types.StructType = new StructType()
    .add("Pixel", new PixelUDT).add("Color", IntegerType)
  override def bufferSchema: StructType = new StructType()
    .add("WeightArray", ArrayType(IntegerType, containsNull = true))
    .add("ResolutionX", IntegerType)
    .add("ResolutionY", IntegerType)
  override def toString: String = s" **${ST_Render.getClass.getName}**  "
  override def dataType: DataType = new ImageWrapperUDT

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit =
  {
    // No need to initialize
    var array = new Array[Int](1)
    array(0)=999
    buffer(0)=array
    buffer(1)=0
    buffer(2)=0
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit =
  {
    var colorArray = buffer.getAs[Seq[Int]](0)
    val inputPixel = input.getAs[Pixel](0)
    val reversedY = inputPixel.getResolutionY - inputPixel.getY -1
    var color = input.getInt(1)
    var currentColorArray: Array[Int] = null
    if(colorArray.length==1)
    {
      // We got an empty image array which just left the initialize function
      currentColorArray = new Array[Int](inputPixel.getResolutionX*inputPixel.getResolutionY)
    }
    else
    {
      currentColorArray = colorArray.toArray
    }
//    if(inputPixel.getX<0 || inputPixel.getX>=inputPixel.getResolutionX || inputPixel.getY<0 || inputPixel.getY>=inputPixel.getResolutionY )
//    {
//      log.warn(s"$inputPixel")
//    }
    currentColorArray(inputPixel.getX.intValue()+reversedY.intValue()*inputPixel.getResolutionX) = color//GenericColoringRule.EncodeToRGB(weight)
    //var image = new BufferedImage(inputPixel.getResolutionX, inputPixel.getResolutionY)
    //image.setData().setRGB(inputPixel.getX, inputPixel.getY, GenericColoringRule.EncodeToRGB(weight))
    buffer(0) = currentColorArray
    buffer(1) = inputPixel.getResolutionX
    buffer(2) = inputPixel.getResolutionY
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit =
  {
    val leftColorArray = buffer1.getAs[Seq[Int]](0)
    val rightColorArray = buffer2.getAs[Seq[Int]](0)
    if (leftColorArray.length==1)
    {
      buffer1(0) = buffer2(0)
      buffer1(1) = buffer2(1)
      buffer1(2) = buffer2(2)
      return
    }
    else if (rightColorArray.length==1)
    {
      return
    }
    val w = buffer1.getAs[Int](1) // This can be rightColorArray. The left and right are expected to have the same resolutions
    val h = buffer1.getAs[Int](2)
    var combinedColorArray = new Array[Int](w*h)
    for (i <- 0 to (w*h-1))
    {
      // We expect that for each i, only one of leftColorArray and RightColorArray has non-zero value.
      combinedColorArray(i) = leftColorArray(i)+rightColorArray(i)
    }
    buffer1(0) = combinedColorArray
    buffer1(1) = w
    buffer1(2) = h
  }

  override def evaluate(buffer: Row): Any =
  {
    val colorArray = buffer.getAs[Seq[Int]](0)
    val w = buffer.getAs[Int](1)
    val h = buffer.getAs[Int](2)
    var bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    for (j <- 0 to h-1)
    {
      for(i <- 0 to w-1)
      {
        bufferedImage.setRGB(i, j, colorArray(i+j*w))
      }
    }
    return new ImageSerializableWrapper(bufferedImage)
  }
}

case class ST_Render_v2() extends UserDefinedAggregateFunction with Logging{
  // This is the input fields for your aggregate function.
  override def inputSchema: org.apache.spark.sql.types.StructType = new StructType()
    .add("Pixel", new PixelUDT).add("Weight", IntegerType)
  override def bufferSchema: StructType = new StructType()
    .add("WeightArray", new ImageWrapperUDT)
//    .add("ResolutionX", IntegerType)
//    .add("ResolutionY", IntegerType)
  override def toString: String = s" **${ST_Render_v2.getClass.getName}**  "
  override def dataType: DataType = new ImageWrapperUDT

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit =
  {
    // No need to initialize
//    var array = new Array[Int](1)
//    array(0)=999
//    buffer(0)=array
//    buffer(1)=0
//    buffer(2)=0
    var image = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB)
    buffer(0) = new ImageSerializableWrapper(image)
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit =
  {
    var image = buffer.getAs[ImageSerializableWrapper](0).getImage
    val inputPixel = input.getAs[Pixel](0)
    if (image.getWidth == 1){
      // This is a new image array
      image = new BufferedImage(inputPixel.getResolutionX,inputPixel.getResolutionY,BufferedImage.TYPE_INT_ARGB)
    }

    val reversedY = inputPixel.getResolutionY - inputPixel.getY -1
    var color = input.getInt(1)
    if(inputPixel.getX<0 || inputPixel.getX>=inputPixel.getResolutionX || inputPixel.getY<0 || inputPixel.getY>=inputPixel.getResolutionY )
    {
      log.warn(s"$inputPixel")
    }
    image.setRGB(inputPixel.getX.intValue(), reversedY.intValue(), color.intValue())
    buffer(0) = new ImageSerializableWrapper(image)
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit =
  {
    val leftImage = buffer1.getAs[ImageSerializableWrapper](0).getImage
    val rightImage = buffer2.getAs[ImageSerializableWrapper](0).getImage
    val w = Math.max(leftImage.getWidth, rightImage.getWidth)
    val h = Math.max(leftImage.getHeight, rightImage.getHeight)
    var combinedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    var graphics = combinedImage.getGraphics
    graphics.drawImage(leftImage, 0, 0, null)
    graphics.drawImage(rightImage, 0, 0, null)
    buffer1(0) = new ImageSerializableWrapper(combinedImage)
  }

  override def evaluate(buffer: Row): Any =
  {
    buffer.getAs[ImageSerializableWrapper](0)
  }
}
/*
 * Copyright (c) 2013-2014 Josef Hardi <josef.hardi@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.obidea.semantika.datatype.exception;

import com.obidea.semantika.exception.SemantikaRuntimeException;

public class DataTypeException extends SemantikaRuntimeException
{
   private static final long serialVersionUID = 629451L;

   /**
    * Create a new DataTypeException with no detailed message.
    */
   public DataTypeException()
   {
      super();
   }

   /**
    * Create a new DataTypeException with the specified message.
    * 
    * @param message
    *           The String that contains a detailed message.
    */
   public DataTypeException(String message)
   {
      super(message);
   }

   /**
    * Create a new DataTypeException with the specified cause.
    * 
    * @param cause
    *           The Throwable object that contains a specified cause.
    */
   public DataTypeException(Throwable cause)
   {
      super(cause);
   }

   /**
    * Create a new DataTypeException with the specified message and
    * generated by the specified cause.
    * 
    * @param message
    *           The String that contains a detailed message.
    * @param cause
    *           The Throwable object that contains a specified cause.
    */
   public DataTypeException(String message, Throwable cause)
   {
      super(message, cause);
   }
}

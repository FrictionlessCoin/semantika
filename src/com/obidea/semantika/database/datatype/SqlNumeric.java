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
package com.obidea.semantika.database.datatype;

import java.math.BigDecimal;
import java.sql.Types;

public class SqlNumeric extends AbstractNumericType<BigDecimal>
{
   private static final SqlNumeric mInstance;

   static {
      mInstance = new SqlNumeric();
   }

   private SqlNumeric()
   {
      super("NUMERIC");
   }

   public static SqlNumeric getInstance()
   {
      return mInstance;
   }

   @Override
   protected BigDecimal parseLexicalForm(String lexicalForm)
   {
      return new BigDecimal(lexicalForm);
   }

   @Override
   public int getType()
   {
      return Types.NUMERIC;
   }
}

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
package com.obidea.semantika.app;

import com.obidea.semantika.database.IDatabase;
import com.obidea.semantika.mapping.EmptyMappingSetFactory;
import com.obidea.semantika.mapping.ParsableMappingSetFactory;
import com.obidea.semantika.ontology.IOntology;

public class MappingLoaderFactory
{
   public static MappingLoader createMappingLoader(IDatabase database, IOntology ontology)
   {
      MappingLoader mappingLoader = new MappingLoader(database, ontology);
      mappingLoader.addMappingSetFactory(new EmptyMappingSetFactory());
      mappingLoader.addMappingSetFactory(new ParsableMappingSetFactory());
      return mappingLoader;
   }
}

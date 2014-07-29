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
package com.obidea.semantika.queryanswer;

import com.obidea.semantika.exception.SemantikaException;
import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.queryanswer.result.IQueryResult;

public interface IQueryEngine
{
   String getName();

   IKnowledgeBase getKnowledgeBase();

   void start() throws QueryEngineException;

   void stop() throws QueryEngineException;

   boolean isStarted();

   IQueryResult evaluate(String sparql) throws SemantikaException;

   String translate(String sql) throws SemantikaException;
}

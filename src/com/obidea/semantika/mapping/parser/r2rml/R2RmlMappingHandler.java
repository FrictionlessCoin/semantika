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
package com.obidea.semantika.mapping.parser.r2rml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import io.github.johardi.r2rmlparser.document.IMappingVisitor;
import io.github.johardi.r2rmlparser.document.LogicalTable;
import io.github.johardi.r2rmlparser.document.ObjectMap;
import io.github.johardi.r2rmlparser.document.PredicateMap;
import io.github.johardi.r2rmlparser.document.PredicateObjectMap;
import io.github.johardi.r2rmlparser.document.RefObjectMap;
import io.github.johardi.r2rmlparser.document.SubjectMap;
import io.github.johardi.r2rmlparser.document.TermMap;

import com.obidea.semantika.datatype.DataType;
import com.obidea.semantika.expression.base.ITerm;
import com.obidea.semantika.expression.base.Literal;
import com.obidea.semantika.expression.base.UriReference;
import com.obidea.semantika.mapping.IMappingFactory.IMetaModel;
import com.obidea.semantika.mapping.UriTemplate;
import com.obidea.semantika.mapping.base.ClassMapping;
import com.obidea.semantika.mapping.base.PropertyMapping;
import com.obidea.semantika.mapping.base.sql.SqlColumn;
import com.obidea.semantika.mapping.parser.AbstractMappingHandler;
import com.obidea.semantika.mapping.parser.R2RmlVocabulary;

public class R2RmlMappingHandler extends AbstractMappingHandler implements IMappingVisitor
{
   public R2RmlMappingHandler(IMetaModel metaModel)
   {
      super(metaModel);
   }

   @Override
   public void visit(LogicalTable arg)
   {
      setSqlQuery(arg.getTableView().getSqlQuery());
   }

   @Override
   public void visit(SubjectMap arg)
   {
      validateSubjectMap(arg);
      setClassUri(arg.getClassIri());
      
      int termMap = arg.getType();
      String value = arg.getValue();
      String termType = arg.getTermType();
      String datatype = arg.getDatatype();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            throw new IllegalR2RmlMappingException("Subject map cannot use column-valued term map"); //$NON-NLS-1$
         case TermMap.CONSTANT_VALUE:
            setSubjectMapValue(getExpressionObjectFactory().getUriReference(createUri(value)));
            break;
         case TermMap.TEMPLATE_VALUE:
            setSubjectMapValue(getTemplateTerm(value, termType, datatype));
            break;
      }
      // Create the class mapping if a class URI specified in the mapping
      if (getClassUri() != null) {
         ClassMapping cm = getMappingObjectFactory().createClassMapping(getClassUri(), getSqlQuery());
         cm.setSubjectMapValue(getSubjectMapValue()); // subject template
         addMapping(cm);
      }
   }

   /*
    * Validation procedure based on http://www.w3.org/TR/r2rml/#termtype
    */
   private void validateSubjectMap(SubjectMap arg)
   {
      String termType = arg.getTermType();
      if (termType.equals(R2RmlVocabulary.LITERAL.getUri())) {
         throw new IllegalR2RmlMappingException("Subject map cannot have term type rr:Literal"); //$NON-NLS-1$
      }
   }

   @Override
   public void visit(PredicateObjectMap arg)
   {
      arg.getPredicateMap().accept(this);
      arg.getObjectMap().accept(this);
      
      PropertyMapping pm = getMappingObjectFactory().createPropertyMapping(getPropertyUri(), getSqlQuery());
      pm.setSubjectMapValue(getSubjectMapValue());
      pm.setObjectMapValue(getObjectMapValue());
      addMapping(pm);
   }

   @Override
   public void visit(PredicateMap arg)
   {
      validatePredicateMap(arg);
      int termMap = arg.getType();
      String value = arg.getValue();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            throw new IllegalR2RmlMappingException("Predicate map cannot use column-valued term map"); //$NON-NLS-1$
         case TermMap.CONSTANT_VALUE:
            setPredicateMapValue(getExpressionObjectFactory().getUriReference(createUri(value)));
            break;
         case TermMap.TEMPLATE_VALUE:
            throw new IllegalR2RmlMappingException("Predicate map cannot use template-valued term map"); //$NON-NLS-1$
      }
   }

   /*
    * Validation procedure based on http://www.w3.org/TR/r2rml/#termtype
    */
   private void validatePredicateMap(PredicateMap arg)
   {
      String termType = arg.getTermType();
      if (termType.equals(R2RmlVocabulary.LITERAL.getUri())) {
         throw new IllegalR2RmlMappingException("Subject map cannot have term type rr:Literal"); //$NON-NLS-1$
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE.getUri())) {
         throw new IllegalR2RmlMappingException("Subject map cannot have term type rr:BlankNode"); //$NON-NLS-1$
      }
   }

   @Override
   public void visit(ObjectMap arg)
   {
      int termMap = arg.getType();
      String value = arg.getValue();
      String termType = arg.getTermType();
      String datatype = arg.getDatatype();
      switch (termMap) {
         case TermMap.COLUMN_VALUE:
            setObjectMapValue(getColumnTerm(value, termType, datatype));
            break;
         case TermMap.CONSTANT_VALUE:
            setObjectMapValue(getLiteralTerm(value, termType, datatype));
            break;
         case TermMap.TEMPLATE_VALUE:
            setObjectMapValue(getTemplateTerm(value, termType, datatype));
            break;
      }
   }

   @Override
   public void visit(RefObjectMap arg)
   {
      // NO-OP
   }

   /*
    * Private utility methods
    */

   private URI getPropertyUri()
   {
      return ((UriReference) getPredicateMapValue()).toUri();
   }

   private SqlColumn getColumnTerm(String columnName, String termType, String datatype)
   {
      if (termType.equals(R2RmlVocabulary.IRI.getUri())) {
         if (StringUtils.isEmpty(datatype)) {
            SqlColumn column = getColumnTerm(columnName);
            column.setUserDatatype(DataType.ANY_URI); // make it as an IRI object
            return column;
         }
         else {
            throw new IllegalR2RmlMappingException("Cannot use rr:datatype together with term type rr:IRI"); //$NON-NLS-1$
         }
      }
      else if (termType.equals(R2RmlVocabulary.LITERAL.getUri())) {
         if (StringUtils.isEmpty(datatype)) {
            return getColumnTerm(columnName); // set as natural RDF literal
         }
         else {
            SqlColumn column = getColumnTerm(columnName);
            column.setUserDatatype(datatype);
            return column; // set as datatype-override RDF literal
         }
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE.getUri())) {
         throw new UnsupportedR2RmlFeatureException("rr:BlankNode"); //$NON-NLS-1$
      }
      else {
         String message = String.format("Unknown term type \"%s\"", termType); //$NON-NLS-1$
         throw new R2RmlParserException(message);
      }
   }

   private SqlColumn getColumnTerm(String columnName)
   {
      SqlColumn column = (SqlColumn) getSqlQuery().findSelectItemExpression(columnName);
      if (column != null) {
         return column;
      }
      String message = String.format("Unknown column name \"%s\"", columnName); //$NON-NLS-1$
      throw new R2RmlParserException(message);
   }

   private ITerm getLiteralTerm(String value, String termType, String datatype)
   {
      if (termType.equals(R2RmlVocabulary.IRI.getUri())) {
         if (StringUtils.isEmpty(datatype)) {
            UriReference uri = getExpressionObjectFactory().getUriReference(createUri(value));
            return uri;
         }
         else {
            throw new IllegalR2RmlMappingException("Cannot use rr:datatype together with term type rr:IRI"); //$NON-NLS-1$
         }
      }
      else if (termType.equals(R2RmlVocabulary.LITERAL.getUri())) {
         if (StringUtils.isEmpty(datatype)) {
            Literal literal = getExpressionObjectFactory().getLiteral(value, DataType.STRING); // by default
            return literal;
         }
         else {
            Literal literal = getExpressionObjectFactory().getLiteral(value, datatype);
            return literal;
         }
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE.getUri())) {
         throw new UnsupportedR2RmlFeatureException("rr:BlankNode"); //$NON-NLS-1$
      }
      else {
         String message = String.format("Unknown term type \"%s\"", termType); //$NON-NLS-1$
         throw new R2RmlParserException(message);  
      }
   }

   private ITerm getTemplateTerm(String value, String termType, String datatype)
   {
      if (termType.equals(R2RmlVocabulary.IRI.getUri())) {
         if (StringUtils.isEmpty(datatype)) {
            R2RmlTemplate template = new R2RmlTemplate(value);
            String templateString = template.getTemplateString();
            List<SqlColumn> parameters = getColumnTerms(template.getColumnNames());
            UriTemplate uriTemplate = getMappingObjectFactory().createUriTemplate(templateString, parameters);
            return uriTemplate;
         }
         else {
            throw new IllegalR2RmlMappingException("Cannot use rr:datatype together with term type rr:IRI"); //$NON-NLS-1$
         }
      }
      else if (termType.equals(R2RmlVocabulary.LITERAL.getUri())) {
         throw new UnsupportedR2RmlFeatureException("rr:template for literal string construction");
      }
      else if (termType.equals(R2RmlVocabulary.BLANK_NODE.getUri())) {
         throw new UnsupportedR2RmlFeatureException("rr:BlankNode as term type"); //$NON-NLS-1$
      }
      else {
         String message = String.format("Unknown term type \"%s\"", termType); //$NON-NLS-1$
         throw new R2RmlParserException(message);
      }
   }

   private List<SqlColumn> getColumnTerms(List<String> columnNames)
   {
      List<SqlColumn> toReturn = new ArrayList<SqlColumn>();
      for (String columnName : columnNames) {
         toReturn.add(getColumnTerm(columnName));
      }
      return toReturn;
   }
}

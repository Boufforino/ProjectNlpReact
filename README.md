Dependance :
____________
Unitex-GramLab3.1

We recommend you to import the project in IntelliJ IDEA in order to launch the server.
Right click Server.scala and choose "run Server"

Routes :
________
"/text" 
-> POST 

	require a json body
	
-> GET 

	return list of texts
	
-> GET ROOT \id

	return text with the matching id
	
-> PUT ROOT / id / changeTitre

	require a json body
	
	change name of  text with the matching id

"/graph" 
-> POST 

	require a json body
	
-> GET 

	return list of graphs
	
-> GET ROOT /id

	return graph with the matching id
	
-> PUT  ROOT/ id / changeTitre

	require a json body
	
	change name of  graph with the matching id

"/nlp"

-> GET ROOT / id1 / id2 / getESN

	apply graph with id2 to text id1 and returns a list of contained ESN

Formating :
___________

.grf files need to be preprocess before being included in a .json

We suggest you look into final.grf and final.json to see the necessary modifications

SPARQL :
________

If you desire to look more in depth in the result, we recommend you to use :

https://dbpedia.org/sparql

here is an exemple if France is a ESN or not :

select distinct ?item where
{
?item rdfs:label ?name.
filter (?name= "France"@fr)
{
 SELECT DISTINCT ?item WHERE
 {
  {
   ?item geo:lat ?b  .
   ?item geo:long ?c .
  } union
  {
   ?item a dbo:Country
  } union
  {
   ?item a dbo:City
  }
 }
}
}




 

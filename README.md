# rest-spring 456
Proyecto de Ejemplo que  expone un api rest en SpringBoot 


Autor Wilmer Guillermo Aguilera



		from("timer://foo?period=5000")
		.setBody().constant("<<Body Proncipal>>")
		.log(">>>Log Principal ${body}").onCompletion().to("direct-vm:log").end().log(">>>Log Principal 2 ${body}");


		from("direct-vm:log")
		.setBody().constant("<<Body log>>")
		.log(">>Log Secundario ${body}");

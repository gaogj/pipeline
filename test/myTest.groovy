import java.util.LinkedHashSet;

def services = ["mysql", "redis", "mountebank", "dealdemoserver", "tester"] as LinkedHashSet<String>;

println services.getClass().toString();

for(def it : services) {
    println "services it:" + it
}

services.each {
    println "services2 it:" + it
}

//println services.stream().filter { it.match("dealdemoserver")}

def matchName = null;
def patten = "tester"

for (def it : services) { 
	println it

    if (it.matches(patten)) { 
        matchName = it; 
        break;
    }
}

println matchName
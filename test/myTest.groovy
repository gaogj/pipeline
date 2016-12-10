import java.util.LinkedHashSet;

def services = ["mysql", "redis", "mountebank", "dealdemoserver"] as LinkedHashSet<String>;

println services.getClass().toString();

for(def it : services) {
    println "services it:" + it
}

services.each {
    println "services2 it:" + it
}

println services.stream().filter { it.match("dealdemoserver")}

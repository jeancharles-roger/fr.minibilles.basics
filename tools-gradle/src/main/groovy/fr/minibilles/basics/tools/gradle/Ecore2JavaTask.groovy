package fr.minibilles.basics.tools.gradle

import fr.minibilles.basics.generation.Merger
import fr.minibilles.basics.tools.model.ModelGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class Ecore2JavaTask extends DefaultTask {

    String description 	= 'Generate Java source code from ECORE model.'

    String source
    String destination
    String basepackage = ""
    String encoding = "UTF-8"

    String visitor = "simple"
    boolean walkerAndCloner = false
    boolean replacer = false
    boolean boost = false

    String sexp = "none"
    String xml = "none"
    boolean model = false

    boolean vectors = false

    String generatorClassName

    @TaskAction
    def generateJava() {
        if ( source == null ) {
            throw new Exception("Source file isn't set (use the 'source' attribute).")
        }
        if ( destination == null ) {
            throw new Exception("Destination folder isn't set (use the 'destination' attribute).")
        }

        if ( "none" != visitor && "simple" != visitor && "recursive" != visitor) {
            throw new Exception("Visitor attribute '"+ visitor +"' is unknown, only 'none', 'simple' and 'recursive' are supported.")
        }

        if ( "none" != sexp && "simple" != sexp && "recursive" != sexp) {
            throw new Exception("SExp attribute '"+ sexp +"' is unknown, only 'none', 'simple' and 'recursive' are supported.")
        }

        if ( "none" != xml && "simple" != xml && "recursive" != xml) {
            throw new Exception("XMl attribute '"+ xml +"' is unknown, only 'none', 'simple' and 'recursive' are supported.")
        }

        if (  "none" != sexp && sexp != visitor) {
            if ("none" == visitor) {
                visitor = sexp
                System.out.println("SExp option '"+ sexp +"' forces visitor '"+ visitor +"'.")
            } else {
                throw new Exception("SExp handling needs a visitor of the same type.")
            }
        }

        if (  "none" != xml && xml != visitor) {
            if ("none" == visitor) {
                visitor = xml
                System.out.println("Xml option '"+ xml +"' forces visitor '"+ visitor +"'.")
            } else {
                throw new Exception("Xml handling needs a visitor of the same type.")
            }
        }

        ModelGenerator generator = null
        // creates model generator with given class name (if given).
        if ( generatorClassName != null && generatorClassName.length() > 0 ) {
            try {
                @SuppressWarnings("unchecked")
                final Class<ModelGenerator> generatorClass = (Class<ModelGenerator>) getClass().getClassLoader().loadClass(generatorClassName)
                generator = generatorClass.newInstance()
            } catch (Throwable ignored) {
                System.err.println("Can't load generator '"+ generatorClassName +"'.")
                generator = null
            }
        }

        // generator wasn't given or failed, set default one.
        if ( generator == null ) {
            generator = new ModelGenerator()
        }

        generator.setSourceFile(new File(source))
        generator.setDestinationFile(new File(destination))
        generator.setBasepackage(basepackage)
        generator.setEncoding(encoding)
        generator.setMerger(Merger.DEFAULT)
        if ( visitor != null && visitor.length() > 0 && "none" != visitor) {
            generator.setGenerateVisitor(true)
            generator.setGenerateWalkerAndCloner(isWalkerAndCloner())
            generator.setGenerateReplacer(isReplacer())
            if ("simple" == visitor) generator.setRecursiveVisitor(false)
            if ("recursive" == visitor) generator.setRecursiveVisitor(true)
        } else {
            generator.setGenerateVisitor(false)
            generator.setRecursiveVisitor(false)
            generator.setGenerateWalkerAndCloner(false)
            generator.setGenerateReplacer(false)
        }
        generator.setGenerateBoost(boost)

        if ( sexp != null && sexp.length() > 0 && "none" != sexp) {
            generator.setGenerateSExp(true)
            if ("simple" == sexp) generator.setRecursiveSerialization(false)
            if ("recursive" == sexp) generator.setRecursiveSerialization(true)
        } else {
            generator.setGenerateSExp(false)
        }

        if ( xml != null && xml.length() > 0 && "none" != xml) {
            generator.setGenerateXml(true)
            if ("simple" == xml) generator.setRecursiveSerialization(false)
            if ("recursive" == xml) generator.setRecursiveSerialization(true)
        } else {
            generator.setGenerateXml(false)
        }

        generator.setGenerateModel(model)

        if ( vectors ) {
            generator.setListImplementation("java.util.Vector")
        }

        try {
            generator.generate()
        } catch (Exception e) {
            throw new Exception(e)
        }
    }
}

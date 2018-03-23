package fr.minibilles.basics.tools.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class Ecore2JavaPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('ecore2java', type: Ecore2JavaTask)
    }
}

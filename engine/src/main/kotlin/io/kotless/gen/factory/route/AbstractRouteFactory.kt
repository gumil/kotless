package io.kotless.gen.factory.route

import io.kotless.URIPath
import io.kotless.gen.GenerationContext
import io.kotless.gen.Names
import io.kotless.gen.factory.apigateway.RestAPIFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_resource

abstract class AbstractRouteFactory {
    private val allResources = HashMap<URIPath, String>()

    fun getResource(resourcePath: URIPath, api: RestAPIFactory.RestAPIOutput, context: GenerationContext): String {
        if (URIPath() !in allResources) {
            allResources[URIPath()] = api.root_resource_id
        }

        var path = URIPath()
        for (part in resourcePath.parts) {
            val prev = path
            path = URIPath(path, part)

            if (path !in allResources) {
                val resource = api_gateway_resource(Names.tf(path.parts)) {
                    rest_api_id = api.rest_api_id
                    parent_id = allResources[prev]!!
                    path_part = part
                }
                context.registerEntities(resource)
                allResources[path] = resource::id.ref
            }
        }

        return allResources[path]!!
    }
}
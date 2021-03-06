package io.kotless.gen.factory.resource.static

import io.kotless.StaticResource
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.hcl.ref
import io.kotless.terraform.functions.*
import io.kotless.terraform.provider.aws.resource.s3.s3_object

object StaticResourceFactory : GenerationFactory<StaticResource, StaticResourceFactory.Output> {
    data class Output(val key: String, val bucket: String)

    override fun mayRun(entity: StaticResource, context: GenerationContext) = true

    override fun generate(entity: StaticResource, context: GenerationContext): GenerationFactory.GenerationResult<Output> {

        val obj = s3_object(context.names.tf(entity.bucket, entity.path.parts)) {
            bucket = entity.bucket
            key = entity.path.toString()
            source = path(entity.file)
            etag = eval(filemd5(entity.file))
            content_type = entity.mime.mimeText
        }

        return GenerationFactory.GenerationResult(Output(obj::key.ref, obj::bucket.ref), obj)
    }
}

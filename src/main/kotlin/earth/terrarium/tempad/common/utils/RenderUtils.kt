package earth.terrarium.tempad.common.utils

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.systems.RenderPass
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.Tesselator
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

/*
fun renderBufferWithPipeline(
    name: String?,
    renderPipeline: RenderPipeline,
    renderTarget: RenderTarget,
    bufferBuilderConsumer: Consumer<BufferBuilder?>,
    uniformAndSamplerConsumer: Consumer<RenderPass?>,
) {
    val mode = renderPipeline.getVertexFormatMode()
    val builder = Tesselator.getInstance().begin(mode, renderPipeline.getVertexFormat())
    bufferBuilderConsumer.accept(builder)
    builder.buildOrThrow().use { meshData ->
        RenderSystem.getDevice().createCommandEncoder().createRenderPass(
            {"Dynamic vertex buffer"},
            renderTarget.colorTextureView!!,
            OptionalInt.empty(),
            renderTarget.depthTextureView,
            OptionalDouble.empty()
        ).use { renderPass ->
            RenderSystem.getDevice().createBuffer(
                { name }, BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, meshData.vertexBuffer()
            ).use { buffer ->
                val autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(mode)
                renderPass.setPipeline(renderPipeline)
                renderPass.setVertexBuffer(0, buffer)
                renderPass.setIndexBuffer(
                    autoStorageIndexBuffer.getBuffer(meshData.drawState().indexCount()),
                    autoStorageIndexBuffer.type()
                )
                uniformAndSamplerConsumer.accept(renderPass)
                renderPass.drawIndexed(0, meshData.drawState().indexCount())
            }
        }
    }
}

fun renderBufferWithPipeline(
    renderPipeline: RenderPipeline,
    renderTarget: RenderTarget,
    bufferBuilderConsumer: Consumer<BufferBuilder?>,
    uniformAndSamplerConsumer: Consumer<RenderPass?>,
) {
    renderBufferWithPipeline(
        "Dynamic vertex buffer",
        renderPipeline,
        renderTarget,
        bufferBuilderConsumer,
        uniformAndSamplerConsumer
    )
}
 */
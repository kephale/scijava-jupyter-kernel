/*-
 * #%L
 * SciJava polyglot kernel for Jupyter.
 * %%
 * Copyright (C) 2017 Hadrien Mary
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.scijava.jupyter.kernel;

import com.twosigma.beaker.jupyter.handler.CommOpenHandler;
import com.twosigma.jupyter.Kernel;
import com.twosigma.jupyter.KernelParameters;
import com.twosigma.jupyter.KernelSocketsFactoryImpl;
import com.twosigma.jupyter.handler.KernelHandler;
import com.twosigma.jupyter.message.Message;
import java.util.HashMap;

import org.scijava.Context;
import org.scijava.jupyter.kernel.comm.ScijavaCommOpenHandler;
import org.scijava.jupyter.kernel.configuration.ScijavaKernelConfigurationFile;
import org.scijava.jupyter.kernel.evaluator.ScijavaEvaluator;
import org.scijava.jupyter.kernel.handler.ScijavaKernelInfoHandler;
import org.scijava.jupyter.service.JupyterService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Hadrien Mary
 */
public class ScijavaKernel extends Kernel {

    // Scijava context
    Context context;

    @Parameter
    private transient LogService log;

    private final ScijavaKernelConfigurationFile config;
    private final ScijavaEvaluator evaluator;

    public ScijavaKernel(final Context context, final String id, final ScijavaEvaluator evaluator,
            ScijavaKernelConfigurationFile config, KernelSocketsFactoryImpl kernelSocketsFactory) {

        super(id, evaluator, kernelSocketsFactory);
        this.context = context;
        this.context.inject(this);
        this.config = config;
        this.evaluator = evaluator;

        this.setLogLevel(config.getLogLevel());
        log.info("Log level used is : " + this.config.getLogLevel());

        log.info("Scijava Kernel is started and ready to use.");
	
	this.setShellOptions(new KernelParameters(new HashMap<>()));
    }

    @Override
    public CommOpenHandler getCommOpenHandler(Kernel kernel) {
        return new ScijavaCommOpenHandler(kernel);
    }

    @Override
    public KernelHandler<Message> getKernelInfoHandler(Kernel kernel) {
        return new ScijavaKernelInfoHandler(kernel);
    }

    private void setLogLevel(String logLevel) {
        switch (logLevel) {
            case "debug":
                this.log.setLevel(LogService.DEBUG);
                break;
            case "error":
                this.log.setLevel(LogService.ERROR);
                break;
            case "info":
                this.log.setLevel(LogService.INFO);
                break;
            case "none":
                this.log.setLevel(LogService.NONE);
                break;
            default:
                this.log.setLevel(LogService.INFO);
                break;
        }
    }

    public static void main(String... args) {
        Context context = new Context();
        
        JupyterService jupyter = context.service(JupyterService.class);
        jupyter.runKernel(args);
        
        context.dispose();
    }
}

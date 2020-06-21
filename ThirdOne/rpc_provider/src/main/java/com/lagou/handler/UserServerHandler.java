package com.lagou.handler;

import com.alibaba.fastjson.JSON;
import com.lagou.service.RpcRequest;
import com.lagou.utils.MyAwareUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

public class UserServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

//        // 判断是否符合约定，符合则调用本地方法，返回数据
//        // msg:  UserService#sayHello#are you ok?
//        if(msg.toString().startsWith("UserService")){
//            UserServiceImpl userService = new UserServiceImpl();
//            String result = userService.sayHello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
//            ctx.writeAndFlush(result);
//        }


        RpcRequest rpcRequest = JSON.parseObject(msg.toString(), RpcRequest.class);
        String className = rpcRequest.getClassName();
        String methodName = rpcRequest.getMethodName();
        System.out.println(className);
        System.out.println(methodName);
        String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
        System.out.println(simpleClassName);

        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();
        String[] beanDefinitionNames = MyAwareUtil.getBeanDefinitionNames();

        // 找出对应的类
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = MyAwareUtil.getBean(beanDefinitionName);
            Class<?> aClass = bean.getClass();

            boolean check = false;
            if (aClass.getSimpleName().equals(simpleClassName)) {
                check = true;
            } else {
                Class<?>[] fatherClasses = aClass.getInterfaces();
                for (Class<?> fatherClass : fatherClasses) {
                    if (fatherClass.getSimpleName().equals(simpleClassName)) {
                        check = true;
                        break;
                    }
                }
            }
            if (!check) {
                continue;
            }

            // 找出待执行的目标方法
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.getName().equals(methodName)) {
                    Class<?>[] parameterTypes1 = declaredMethod.getParameterTypes();
                    if (parameterTypes.length != parameterTypes1.length) {
                        continue;
                    }

                    boolean flag = true;
                    for (int i =0; i < parameterTypes1.length; i++) {
                        if (parameterTypes[i] != parameterTypes1[i]) {
                            flag = false;
                            break;
                        }
                    }
                    // 若方法以及参数都对应的上，则执行方法
                    if (flag) {
                        Object invoke = declaredMethod.invoke(bean, parameters);
                        ctx.writeAndFlush(invoke.toString());
                        break;
                    }
                }
            }
        }
    }
}

m=input('输入层数m=');
n=input('输入层数n=');
H=cell(m-1,1);
%按照层次模型结构，输入判断矩阵
%输入的判断矩阵赋值为cell数组G的元素
%如G（1，1）元素代表第一层第一个准则与第二层间建立的判断矩阵，……
%G（x，y）就表示第x层第y个准则与第x+1层间建立的判断矩阵……
%以此类推
G=cell(1,4);
G{1,1}=[0.5 0.6033:0.3967 0.5];
for i=1:m-1
    f=length(G{i,1});
    H{i,1}(f,1)=0;
    for j=1:n
        if isempty({G{i,j}})==0
            T=tzx1(G{i,j});
            yzx(G{i,j},T);
            if j<2
                H{i,1}=T;
            else
                H{i,1}=[H{i,1},T];
            end
        end
    end
end
YOU=1;
for k=1:m-1
   YOU=TOU*H{m-k,1}; 
end
SQ=YOU;
disp('总体优先级为');
disp(SQ);

function T=tzx1(A)
n=length(A);
c(1:n)=1;
for j=1:n
    for i=1:n
        c(j)=c(j)*A(j,i);
    end
    c(j)=c(j)^(1/n);
end
y=sum(c);
for x=1:n
    c(x)=c(x)/y;
end
T=c';
end

function yzx(A,T)
MW=A*T;
n=length(A);
L=0;
for i=1:n
    L=L+MW(i)/T(i);
end
Max_T=L/n;
CI=(Max_T-n)/(n-1);
RI=[0,0,0.58,0.96,1.12,1.24,1.32,1.41,1.45];
CR=CI/RI(n);
if CR<0.1
    disp('矩阵');disp(A);
    disp('CI=');disp(CI);
    disp('CR=');disp(CR);
    fprintf('%s\n','一致性较好，可以接受');
else
    disp('矩阵');disp(A);
    disp('CI=');disp(CI);
    disp('CR=');disp(CR);
    fprintf('%s\n','一致性较好，可以接受');  
end
end

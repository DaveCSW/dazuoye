disp('请输入判断矩阵A(n阶)'); 
A=input('A='); 
[n,n]=size(A); 
x=ones(n,100); 
y=ones(n,100); 
m=zeros(1,100); 
m(1)=max(x(:,1)); 
y(:,1)=x(:,1); 
x(:,2)=A*y(:,1); 
m(2)=max(x(:,2)); 
y(:,2)=x(:,2)/m(2); p=0.0001;i=2;k=abs(m(2)-m(1)); 
while k>p 
i=i+1; 
x(:,i)=A*y(:,i-1); 
m(i)=max(x(:,i)); 
y(:,i)=x(:,i)/m(i); 
k=abs(m(i)-m(i-1)); end 
a=sum(y(:,i)); 
w=y(:,i)/a; 
t=m(i); 
disp(w);disp(t); 
%以下是一致性检验 
CI=(t-n)/(n-1);RI=[0 0 0.52 0.89 1.12 1.26 1.36 1.41 1.46 1.49 1.52 1.54 1.56 1.58 
1.59]; 
CR=CI/RI(n); 
if CR<0.10 
disp('此矩阵的一致性可以接受!'); 
disp('CI=');disp(CI); 
disp('CR=');disp(CR); end 
function AHPInit1(x,y) %层次分析的初始化 
%默认只有两层 x为准则数，y为方案数 
%CToT为准则对目标生成的比较阵 
%EigOfCri为准则层的特征向量 
%EigOfOpt为选项层的特征向量 
EigOfCri=zeros(x,1);%准则层的特征向量 
EigOfOpt=zeros(y,x); 
dim=x;%维度 
RI=[0 0 0.58 0.90 1.12 1.24 1.32 1.41 1.45 1.49 1.51];%RI标准 %生成成对比较阵 
for i=1:dim 
CToT(i,:)=input('请输入数据:'); end 
CToT %输出 
pause, 
tempmatrix=zeros(x+1); tempmatrix=AHP1(dim,CToT); EigOfCri=tempmatrix(1:x); ci1=tempmatrix(1+x); 
EigOfCri 
ci1 
pause, 
matrix=cell(x);%元胞数组 
ci=zeros(1,x); 
dim=y; 
for k=1:x 
matrix{k}=zeros(dim,dim); 
%生成成对比较阵 
for i=1:dim 
matrix{k}(i,:)=input('请输入数据:'); end 
%判断该比较阵是不是一致阵 
tempmatrix=zeros(y+1); tempmatrix=AHP1(dim,matrix{k}); 
EigOfOpt(:,k)=tempmatrix(1:y); ci(k)=tempmatrix(y+1); EigOfOpt(:,k) 
ci(k) 
pause, 
end 
%下面进行组合一致性检查 
RI=[0 0 0.58 0.90 1.12 1.24 1.32 1.41 1.45 1.49 1.51]; 
CR=ci1/RI(x)+ci*EigOfCri/RI(y); 
CR 
if CR>0.1 
disp('组合一致性不通过，请重新评分') 
return 
end 
%下面根据比较阵的结果进行组合 
result=EigOfOpt*EigOfCri; result 
function f=AHP1(dim,CmpMatrix) 
RI=[0 0 0.58 0.90 1.12 1.24 1.32 1.41 1.45 1.49 1.51]; 
%判断该比较阵是不是一致阵 
%判断该比较阵是不是一致阵 
[V,D]=eig(CmpMatrix);%求得特征向量和特征值 %求出最大特征值和它所对应的特征向量 
tempNum=D(1,1); 
pos=1; 
for h=1:dim 
if D(h,h)>tempNum 
tempNum=D(h,h); 
pos=h; 
end 
end 
eigVector=V(:,pos); maxeig=D(pos,pos); 
maxeig 
dim 
CI=(maxeig-dim)/(dim-1); CR=CI/RI(dim); 
if CR>0.1 
disp('准则对目标影响度评分生成的矩阵不是一致阵，请重新评分') 
return 
end 
CI 
%归一化 
sum=0; 
for h=1:dim 
sum=sum+eigVector(h); end 
sum 
pause, 
for h=1:dim 
eigVector(h)=eigVector(h)/sum; 
end 
f=[eigVector;CI]; 
end


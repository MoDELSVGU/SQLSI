let SessionLoad = 1
let s:so_save = &g:so | let s:siso_save = &g:siso | setg so=0 siso=0 | setl so=-1 siso=-1
let v:this_session=expand("<sfile>:p")
silent only
silent tabonly
cd ~/coding/SQLSI
if expand('%') == '' && !&modified && line('$') <= 1 && getline(1) == ''
  let s:wipebuf = bufnr('%')
endif
let s:shortmess_save = &shortmess
if &shortmess =~ 'A'
  set shortmess=aoOA
else
  set shortmess=aoO
endif
badd +35 src/main/java/org/vgu/sqlsi/Main.java
badd +191 src/main/java/org/vgu/sqlsi/utils/FunctionUtils.java
badd +122 src/main/java/org/vgu/sqlsi/sql/func/AuthFunc.java
badd +99 src/main/java/org/vgu/sqlsi/main/SqlSI.java
badd +54 src/main/java/org/vgu/sqlsi/utils/PrintingUtils.java
badd +130 src/main/java/org/vgu/sqlsi/sql/func/AuthRoleFunc.java
badd +30 src/main/java/org/vgu/sqlsi/sec/SecUnitRule.java
badd +82 src/main/java/org/vgu/sqlsi/sql/func/SQLFunction.java
badd +39 src/main/java/org/vgu/sqlsi/sql/func/CreateFunction.java
argglobal
%argdel
$argadd ~/coding/SQLSI/
edit src/main/java/org/vgu/sqlsi/Main.java
argglobal
balt src/main/java/org/vgu/sqlsi/sql/func/CreateFunction.java
setlocal fdm=manual
setlocal fde=
setlocal fmr={{{,}}}
setlocal fdi=#
setlocal fdl=0
setlocal fml=1
setlocal fdn=20
setlocal fen
silent! normal! zE
let &fdl = &fdl
let s:l = 35 - ((15 * winheight(0) + 23) / 47)
if s:l < 1 | let s:l = 1 | endif
keepjumps exe s:l
normal! zt
keepjumps 35
normal! 071|
tabnext 1
if exists('s:wipebuf') && len(win_findbuf(s:wipebuf)) == 0 && getbufvar(s:wipebuf, '&buftype') isnot# 'terminal'
  silent exe 'bwipe ' . s:wipebuf
endif
unlet! s:wipebuf
set winheight=1 winwidth=20
let &shortmess = s:shortmess_save
let s:sx = expand("<sfile>:p:r")."x.vim"
if filereadable(s:sx)
  exe "source " . fnameescape(s:sx)
endif
let &g:so = s:so_save | let &g:siso = s:siso_save
set hlsearch
nohlsearch
doautoall SessionLoadPost
unlet SessionLoad
" vim: set ft=vim :

import express from 'express'; import fetch from 'node-fetch';
const app=express(); app.set('view engine','ejs'); app.set('views','./views');
const BACKEND = process.env.BACKEND_URL;
app.get('/', async (req,res)=>{ let status='offline'; try{ const r=await fetch(BACKEND+'/api/status'); if(r.ok) status=await r.text(); }catch{} res.render('index',{status}); });
app.get('/about',(r,s)=>s.render('about'));
app.listen(3000,()=>console.log('MPA on 3000'));

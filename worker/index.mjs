import amqp from 'amqplib'; import fetch from 'node-fetch';
const URL = process.env.RABBITMQ_URL; const QUEUE = process.env.RABBITMQ_QUEUE || 'pwm.checks';
if(!URL){ console.error('RABBITMQ_URL missing'); process.exit(1); }

function sha1Hex(s){ const crypto = await import('node:crypto'); return crypto.createHash('sha1').update(s).digest('hex').toUpperCase(); }

(async function(){
  const conn = await amqp.connect(URL); const ch = await conn.createChannel(); await ch.assertQueue(QUEUE,{durable:true});
  console.log('worker up, waiting for jobs…');
  ch.consume(QUEUE, async msg => {
    const body = msg.content.toString(); console.log('job', body);
    try{
      const payload = JSON.parse(body);
      if(payload.pw){
        const crypto = await import('node:crypto');
        const hash = crypto.createHash('sha1').update(payload.pw).digest('hex').toUpperCase();
        const prefix = hash.slice(0,5); const suffix = hash.slice(5);
        const res = await fetch(`https://api.pwnedpasswords.com/range/${prefix}`);
        const text = await res.text();
        const pwned = text.split('\n').some(l => l.startsWith(suffix));
        if(pwned) console.log('⚠️ Pwned password detected for', payload.email || 'unknown');
      }
      ch.ack(msg);
    }catch(e){ console.error('job failed', e); ch.nack(msg,false,false); }
  });
})();

const fs = require('fs');
const zlib = require('zlib');

const W = 1600, H = 600, S = 2;
const data = Buffer.alloc(W * H * 4, 255);
const navy = [6, 43, 79], green = [0, 155, 77], gold = [245, 179, 53], red = [229, 59, 52], white = [255, 255, 255];
const px = (x, y, c) => { x = Math.round(x * S); y = Math.round(y * S); for (let dy = 0; dy < S; dy++) for (let dx = 0; dx < S; dx++) { const xx = x + dx, yy = y + dy; if (xx >= 0 && xx < W && yy >= 0 && yy < H) { const i = (yy * W + xx) * 4; data[i] = c[0]; data[i + 1] = c[1]; data[i + 2] = c[2]; data[i + 3] = 255; } } };
const disc = (x, y, r, c) => { for (let yy = -r; yy <= r; yy++) for (let xx = -r; xx <= r; xx++) if (xx * xx + yy * yy <= r * r) px(x + xx, y + yy, c); };
const line = (x1, y1, x2, y2, w, c) => { const n = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)) * 2; for (let i = 0; i <= n; i++) { const t = i / n; disc(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t, w / 2, c); } };
const arc = (x, y, r, a1, a2, w, c) => { for (let a = a1; a <= a2; a += 0.35) disc(x + Math.cos(a * Math.PI / 180) * r, y + Math.sin(a * Math.PI / 180) * r, w / 2, c); };
const glyphs = {
  M:['10001','11011','10101','10001','10001','10001','10001'], z:['00000','01110','00001','00110','01000','10000','11111'], a:['00000','01110','00001','01111','10001','10001','01111'], n:['00000','11110','10001','10001','10001','10001','10001'], s:['00000','01111','10000','01110','00001','00001','11110'], i:['00100','00000','01100','00100','00100','00100','01110'], C:['01111','10000','10000','10000','10000','10000','01111'], o:['00000','01110','10001','10001','10001','10001','01110'], e:['00000','01110','10001','11111','10000','10001','01110'], c:['00000','01110','10000','10000','10000','10000','01110'], t:['00100','00100','01110','00100','00100','00101','00010']
};
const text = (word, x, y, size, c) => { for (const ch of word) { const g = glyphs[ch]; for (let row = 0; row < 7; row++) for (let col = 0; col < 5; col++) if (g[row][col] === '1') for (let yy = 0; yy < size; yy++) for (let xx = 0; xx < size; xx++) px(x + col * size + xx, y + row * size + yy, c); x += size * 6; } };
// emblem
arc(150, 150, 88, 0, 360, 10, navy); arc(150, 150, 66, 18, 160, 13, green); arc(150, 150, 66, 198, 340, 13, gold);
const nodes = [[98,166,red],[127,149,green],[156,164,gold],[190,128,green],[218,145,red]];
for (let i = 0; i < nodes.length - 1; i++) line(nodes[i][0], nodes[i][1], nodes[i+1][0], nodes[i+1][1], 8, navy);
nodes.forEach(n => disc(n[0], n[1], 11, n[2])); arc(150, 203, 29, 197, 343, 8, navy);
text('Mzansi', 280, 72, 17, navy); text('Connect', 280, 174, 14, green); line(282, 245, 585, 245, 5, gold); disc(600, 245, 5, red);

const crc = b => { let c = 0xffffffff; for (const v of b) { c ^= v; for (let k = 0; k < 8; k++) c = (c >>> 1) ^ (0xedb88320 & -(c & 1)); } return (c ^ 0xffffffff) >>> 0; };
const chunk = (type, body) => { const t = Buffer.from(type); const out = Buffer.alloc(12 + body.length); out.writeUInt32BE(body.length, 0); t.copy(out, 4); body.copy(out, 8); out.writeUInt32BE(crc(Buffer.concat([t, body])), 8 + body.length); return out; };
const raw = Buffer.alloc((W * 4 + 1) * H); for (let y = 0; y < H; y++) { raw[y * (W * 4 + 1)] = 0; data.copy(raw, y * (W * 4 + 1) + 1, y * W * 4, (y + 1) * W * 4); }
const header = Buffer.alloc(13); header.writeUInt32BE(W, 0); header.writeUInt32BE(H, 4); header[8] = 8; header[9] = 6;
fs.writeFileSync('mzansi-connect-logo.png', Buffer.concat([Buffer.from([137,80,78,71,13,10,26,10]), chunk('IHDR', header), chunk('IDAT', zlib.deflateSync(raw, { level: 9 })), chunk('IEND', Buffer.alloc(0))]));

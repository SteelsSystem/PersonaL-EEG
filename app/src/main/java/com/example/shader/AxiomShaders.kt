package com.example.shader

import com.example.model.ShaderPreset

object AxiomShaders {

    const val VS_SOURCE = """
        attribute vec2 a_position;
        varying vec2 v_uv;
        void main() {
            v_uv = (a_position + 1.0) * 0.5;
            gl_Position = vec4(a_position, 0.0, 1.0);
        }
    """

    private const val FS_HEADER = """
        precision highp float;
        varying vec2 v_uv;
        uniform vec2 u_resolution;
        uniform float u_time;
        uniform vec2 u_mouse;
        uniform int u_max_steps;
        uniform float u_hex_scale;
        uniform float u_reflex_density;
        uniform float u_mag_pressure;
        uniform float u_antimag_pulse;
        uniform float u_perturbation;
        uniform float u_heal_wave;
        uniform int u_fxaa;
        uniform vec3 u_rgb_tint;
        uniform float u_unicritic_drift;
        uniform float u_rot_freq;
        uniform float u_rot_phase;
    """

    const val FS_HEX_REFLEX = FS_HEADER + """
        const float TIME_MAG_Z_DRIFT   = 0.4;
        const float TIME_MAG_VX        = 0.7;
        const float TIME_MAG_VY        = 0.8;
        const float TIME_MAG_VZ        = 0.6;
        const float TIME_FRACTURE_A    = 8.0;
        const float TIME_FRACTURE_B    = 7.0;
        const float TIME_Z_SCROLL      = 0.7;
        const float TIME_BREATH        = 2.4;
        const float TIME_RING          = 1.8;
        const float TIME_CAM_YAW       = 0.2;
        const float TIME_CAM_PITCH     = 0.15;
        const float Z_PERIOD           = 1.6;

        float g_magZ, g_magVx, g_magVy, g_magVz;
        float g_fracA, g_fracB, g_zScroll, g_breath, g_ring;
        float g_camYaw, g_camPitch;

        void initTime() {
            g_magZ    = u_time * TIME_MAG_Z_DRIFT;
            g_magVx   = u_time * TIME_MAG_VX;
            g_magVy   = u_time * TIME_MAG_VY;
            g_magVz   = u_time * TIME_MAG_VZ;
            g_fracA   = u_time * TIME_FRACTURE_A;
            g_fracB   = u_time * TIME_FRACTURE_B;
            g_zScroll = u_time * TIME_Z_SCROLL;
            g_breath  = u_time * TIME_BREATH;
            g_ring    = u_time * TIME_RING;
            g_camYaw   = sin(u_rot_phase * 0.35 + u_time * TIME_CAM_YAW) * 0.22;
            g_camPitch = cos(u_rot_phase * 0.28 + u_time * TIME_CAM_PITCH) * 0.14;
        }

        mat2 rot2D(float a) {
            float s = sin(a), c = cos(a);
            return mat2(c, -s, s, c);
        }

        vec3 magneticVectorField(vec3 p, float t) {
            vec3 q = p * 1.35 + vec3(0.0, 0.0, g_magZ);
            float vx = sin(q.y * 2.1 + t) - cos(q.z * 1.9 - g_magVx);
            float vy = sin(q.z * 2.1 - t) - cos(q.x * 1.9 + g_magVy);
            float vz = sin(q.x * 2.1 + g_magVz) - cos(q.y * 1.9 - t);
            return vec3(vx, vy, vz) * u_mag_pressure * 0.14;
        }

        vec4 hexDomainRepetition(vec2 p) {
            const vec2 s = vec2(1.0, 1.7320508);
            vec4 hC = floor(vec4(p, p - vec2(0.5, 1.0)) / s.xyxy) + 0.5;
            vec4 h = vec4(p - hC.xy * s, p - (hC.zw + 0.5) * s);
            return dot(h.xy, h.xy) < dot(h.zw, h.zw)
                   ? vec4(h.xy, hC.xy)
                   : vec4(h.zw, hC.zw + 0.5);
        }

        float sdHexagon2D(vec2 p, float r) {
            const vec3 k = vec3(-0.866025404, 0.5, 0.577350269);
            p = abs(p);
            p -= 2.0 * min(dot(k.xy, p), 0.0) * k.xy;
            p -= vec2(clamp(p.x, -k.z * r, k.z * r), r);
            return length(p) * sign(p.y);
        }

        float evaluateHexReflexLattice(vec3 pos, out float reflexCore, out float boundaryShield, out float healGlow) {
            vec3 magOffset = magneticVectorField(pos, u_time);
            vec3 p = pos + magOffset;

            if (u_perturbation > 0.001) {
                float fractureNoise = sin(p.x * 9.0 + g_fracA) * cos(p.y * 9.0 - g_fracB) * sin(p.z * 9.0);
                p += fractureNoise * u_perturbation * 0.28;
            }

            // Unicritic Critical Drift spatial phase shear
            if (u_unicritic_drift > 0.02) {
                p.x += sin(p.y * 12.0 + u_time * 9.5) * u_unicritic_drift * 0.11;
                p.y += cos(p.z * 10.0 - u_time * 8.0) * u_unicritic_drift * 0.09;
            }

            // Smooth phase-integrated Rotating Frequency (u_rot_phase + helical z-twist)
            float rotFreqClamped = clamp(u_rot_freq, 0.05, 5.0);
            float helicalTwist = u_rot_phase + pos.z * (0.22 * rotFreqClamped);
            vec2 rotatedXY = rot2D(helicalTwist) * p.xy;

            vec2 hexP = rotatedXY * u_hex_scale;
            vec4 hexCell = hexDomainRepetition(hexP);
            vec2 localXY = rot2D(-u_rot_phase * 0.35) * hexCell.xy;
            vec2 cellID = hexCell.zw;

            float cellPhase = fract(sin(dot(cellID, vec2(12.9898, 78.233))) * 43758.5453);
            float localZ = mod(p.z + g_zScroll + cellPhase * Z_PERIOD, Z_PERIOD) - 0.5 * Z_PERIOD;

            float breathWave = sin(g_breath + length(cellID) * 0.85 - pos.z * 1.2);
            float antiMagRadius = 0.34 + 0.07 * breathWave * u_antimag_pulse;

            float hexDist2D = abs(sdHexagon2D(localXY, antiMagRadius)) - 0.028;
            float zRingDist = abs(localZ) - (0.18 + 0.06 * sin(g_ring + cellPhase * 6.28));
            float latticeSDF = max(hexDist2D, zRingDist);

            float innerStruts = abs(sdHexagon2D(localXY * 2.2, 0.25)) - 0.012;
            latticeSDF = min(latticeSDF, max(innerStruts, abs(localZ) - 0.08));

            float radialDist = length(vec3(localXY, localZ * 0.85));
            reflexCore = exp(-radialDist * radialDist * 18.0) * u_reflex_density;

            float shieldShell = abs(radialDist - antiMagRadius * 0.85);
            boundaryShield = smoothstep(0.09, 0.0, shieldShell) * u_antimag_pulse * (0.6 + 0.4 * breathWave);

            float waveRadius = length(pos.xy) - u_heal_wave * 6.5;
            healGlow = exp(-waveRadius * waveRadius * 4.5) * step(0.01, u_heal_wave) * step(u_heal_wave, 1.4);

            return latticeSDF / u_hex_scale;
        }

        vec3 renderHexReflex(vec2 uv) {
            // Apply Unicritic UV wave shear when critical drift is detected
            vec2 warpedUv = uv;
            if (u_unicritic_drift > 0.02) {
                float scanShear = sin(uv.y * 48.0 + u_time * 14.0) * cos(uv.y * 19.0 - u_time * 7.0);
                warpedUv.x += scanShear * u_unicritic_drift * 0.028;
            }

            // Widescreen-first normalization: guarantees full panoramic FOV on both 16:9 widescreen and portrait
            float minRes = max(min(u_resolution.x, u_resolution.y), 1.0);
            vec2 p = ((warpedUv * 2.0 - 1.0) * u_resolution) / minRes;

            float yaw = (u_mouse.x / max(u_resolution.x, 1.0) - 0.5) * 3.14 + g_camYaw;
            float pitch = (u_mouse.y / max(u_resolution.y, 1.0) - 0.5) * 1.6 + g_camPitch;

            vec3 ro = vec3(0.0, 0.0, -2.5);
            vec3 rd = normalize(vec3(p, 1.65));
            rd.yz = rot2D(pitch) * rd.yz;
            rd.xy = rot2D(yaw) * rd.xy;

            vec3 accumColor = vec3(0.01, 0.015, 0.03);
            float t = 0.15;
            float tmax = 9.0;

            for (int i = 0; i < 256; i++) {
                if (i >= u_max_steps || t > tmax) break;

                vec3 pos = ro + rd * t;
                float reflexCore = 0.0;
                float boundaryShield = 0.0;
                float healGlow = 0.0;

                float d = evaluateHexReflexLattice(pos, reflexCore, boundaryShield, healGlow);
                float stepSize = max(abs(d) * 0.58, 0.018);

                vec3 reflexCol = mix(vec3(0.0, 0.95, 1.0), vec3(1.0, 0.75, 0.2), clamp(reflexCore * 0.6, 0.0, 1.0)) * u_rgb_tint;
                accumColor += reflexCol * reflexCore * stepSize * 2.4 * exp(-t * 0.28);

                vec3 shieldCol = mix(vec3(0.95, 0.05, 0.45), vec3(0.55, 0.15, 1.0), 0.5 + 0.5 * sin(pos.z * 2.0 + u_rot_phase)) * u_rgb_tint;
                accumColor += shieldCol * boundaryShield * stepSize * 3.2 * exp(-t * 0.3);

                if (u_perturbation > 0.01) {
                    accumColor += vec3(1.0, 0.25, 0.1) * u_perturbation * 0.012 * exp(-abs(d) * 20.0);
                }

                if (healGlow > 0.001) {
                    accumColor += vec3(0.0, 1.0, 0.65) * healGlow * stepSize * 5.5;
                }

                if (abs(d) < 0.004) {
                    float depthFade = exp(-t * 0.35);
                    vec3 wallColor = mix(vec3(0.08, 0.55, 0.85) * u_rgb_tint, vec3(0.0, 1.0, 0.65), clamp(healGlow * 2.0, 0.0, 1.0));
                    accumColor += wallColor * 0.22 * depthFade;
                }

                t += stepSize;
            }

            // Unicritic Chromatic Aberration & Interference Pulse
            if (u_unicritic_drift > 0.02) {
                float band = 0.5 + 0.5 * sin(uv.y * 90.0 - u_time * 16.0);
                accumColor.r += u_unicritic_drift * 0.28 * band;
                accumColor.b *= (1.0 - u_unicritic_drift * 0.35);
            }

            accumColor = accumColor / (1.0 + accumColor * 0.55);
            accumColor = pow(clamp(accumColor, 0.0, 1.0), vec3(0.88));
            return accumColor;
        }

        void main() {
            initTime();
            vec3 col = vec3(0.0);
            if (u_fxaa == 1) {
                vec2 off = vec2(0.35) / max(u_resolution, vec2(1.0));
                col += renderHexReflex(v_uv + vec2(-off.x, -off.y)) * 0.25;
                col += renderHexReflex(v_uv + vec2( off.x, -off.y)) * 0.25;
                col += renderHexReflex(v_uv + vec2(-off.x,  off.y)) * 0.25;
                col += renderHexReflex(v_uv + vec2( off.x,  off.y)) * 0.25;
            } else {
                col = renderHexReflex(v_uv);
            }
            gl_FragColor = vec4(col, 1.0);
        }
    """

    const val FS_FRACTAL = FS_HEADER + """
        float mandelbulbSDF(vec3 pos, out vec4 outColor) {
            vec3 z = pos;
            float dr = 1.0;
            float r = 0.0;
            float power = (8.0 + sin(u_rot_phase * 0.5) * 1.5) + u_perturbation * 2.5 + u_unicritic_drift * 1.8;

            for (int i = 0; i < 8; i++) {
                r = length(z);
                if (r > 3.2) break;
                float theta = acos(clamp(z.z / max(r, 0.0001), -1.0, 1.0));
                float phi = atan(z.y, z.x);
                dr = pow(r, power - 1.0) * power * dr + 1.0;
                float zr = pow(r, power);
                theta *= power;
                phi *= power;
                z = zr * vec3(sin(theta)*cos(phi), sin(phi)*sin(theta), cos(theta)) + pos;
            }
            outColor = vec4(clamp(length(z) * 0.25, 0.0, 1.0), clamp(abs(z.y)*0.35, 0.0, 1.0), clamp(dr * 0.03, 0.0, 1.0), 1.0);
            return 0.5 * log(max(r, 1.0001)) * r / max(dr, 0.0001);
        }

        vec3 calcNormal(vec3 p) {
            const float eps = 0.0015;
            vec4 d0;
            float d = mandelbulbSDF(p, d0);
            return normalize(vec3(
                mandelbulbSDF(p + vec3(eps, 0.0, 0.0), d0) - d,
                mandelbulbSDF(p + vec3(0.0, eps, 0.0), d0) - d,
                mandelbulbSDF(p + vec3(0.0, 0.0, eps), d0) - d
            ));
        }

        void main() {
            vec2 uv = v_uv;
            if (u_unicritic_drift > 0.02) {
                uv.x += sin(uv.y * 40.0 + u_time * 12.0) * u_unicritic_drift * 0.025;
            }
            float minRes = max(min(u_resolution.x, u_resolution.y), 1.0);
            vec2 p = ((uv * 2.0 - 1.0) * u_resolution) / minRes;
            float angleY = (u_mouse.x / max(u_resolution.x, 1.0) - 0.5) * 6.28 + u_rot_phase;
            float angleX = (u_mouse.y / max(u_resolution.y, 1.0) - 0.5) * 1.8;
            float camDist = 2.7 / max(u_hex_scale * 0.55, 0.4);

            vec3 ro = vec3(camDist * cos(angleY) * cos(angleX), camDist * sin(angleX), camDist * sin(angleY) * cos(angleX));
            vec3 ww = normalize(-ro);
            vec3 uu = normalize(cross(ww, vec3(0.0, 1.0, 0.0)));
            vec3 vv = normalize(cross(uu, ww));
            vec3 rd = normalize(p.x * uu + p.y * vv + 1.5 * ww);

            vec3 col = vec3(0.02, 0.03, 0.06);
            float t = 0.4;
            vec4 trap;
            bool hit = false;

            for (int i = 0; i < 256; i++) {
                if (i >= u_max_steps || t > 5.0) break;
                float d = mandelbulbSDF(ro + rd * t, trap);
                if (d < 0.0015 * t) { hit = true; break; }
                t += d * 0.65;
            }

            if (hit) {
                vec3 pos = ro + rd * t;
                vec3 nor = calcNormal(pos);
                vec3 light = normalize(vec3(0.8, 0.9, -0.6));
                float diff = max(dot(nor, light), 0.0);
                vec3 base = mix(vec3(0.0, 0.9, 1.0), vec3(1.0, 0.1, 0.45), trap.x) * u_rgb_tint;
                col = base * (0.15 + diff * 1.1 * u_reflex_density);
                if (u_heal_wave > 0.01) {
                    col = mix(col, vec3(0.0, 1.0, 0.65), clamp(u_heal_wave * 0.5, 0.0, 0.8));
                }
            }
            if (u_unicritic_drift > 0.02) {
                col.r += u_unicritic_drift * 0.25;
            }
            gl_FragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
        }
    """

    const val FS_VORONOI = FS_HEADER + """
        mat2 rot2D(float a) {
            float s = sin(a), c = cos(a);
            return mat2(c, -s, s, c);
        }

        vec3 hash3(vec3 p) {
            p = vec3(dot(p, vec3(127.1, 311.7, 74.7)),
                     dot(p, vec3(269.5, 183.3, 246.1)),
                     dot(p, vec3(113.5, 271.9, 124.6)));
            return fract(sin(p) * 43758.5453123);
        }

        float voronoi3D(vec3 p, out vec3 cellID) {
            vec3 i = floor(p);
            vec3 f = fract(p);
            float minDist = 1.0;
            vec3 bestID = vec3(0.0);
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    for (int x = -1; x <= 1; x++) {
                        vec3 g = vec3(float(x), float(y), float(z));
                        vec3 o = hash3(i + g);
                        o = 0.5 + 0.45 * sin(u_rot_phase + 6.2831 * o + (u_perturbation + u_unicritic_drift) * 4.0);
                        vec3 r = g + o - f;
                        float d = dot(r, r);
                        if (d < minDist) { minDist = d; bestID = i + g; }
                    }
                }
            }
            cellID = bestID;
            return sqrt(minDist);
        }

        void main() {
            vec2 uv = v_uv;
            if (u_unicritic_drift > 0.02) {
                uv.x += sin(uv.y * 42.0 + u_time * 13.0) * u_unicritic_drift * 0.024;
            }
            float minRes = max(min(u_resolution.x, u_resolution.y), 1.0);
            vec2 p = ((uv * 2.0 - 1.0) * u_resolution) / minRes;
            vec3 ro = vec3(0.0, 0.0, 3.4);
            vec3 rd = normalize(vec3(p, -1.7));
            rd.xy = rot2D(u_rot_phase * 0.45) * rd.xy;
            vec3 col = vec3(0.02, 0.04, 0.08);
            float t = 0.6;
            vec3 cellID;

            for (int i = 0; i < 256; i++) {
                if (i >= u_max_steps || t > 6.5) break;
                vec3 pos = ro + rd * t;
                pos.xy = rot2D(u_rot_phase * 0.35 + pos.z * 0.18) * pos.xy;
                float v = voronoi3D(pos * u_hex_scale, cellID);
                float d = (length(pos) - 1.8) + (1.0 - v) * 0.55;
                if (d < 0.0025 * t) {
                    vec3 c = (0.5 + 0.5 * sin(cellID * 2.3 + vec3(0.0, 1.5, 3.1))) * u_rgb_tint;
                    col = c * u_reflex_density * exp(-t * 0.18);
                    break;
                }
                t += d * 0.55;
            }
            if (u_unicritic_drift > 0.02) {
                col.r += u_unicritic_drift * 0.22;
            }
            gl_FragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
        }
    """

    fun getSourceForPreset(preset: ShaderPreset): String = when (preset) {
        ShaderPreset.HEX_REFLEX -> FS_HEX_REFLEX
        ShaderPreset.FRACTAL -> FS_FRACTAL
        ShaderPreset.VORONOI -> FS_VORONOI
    }
}

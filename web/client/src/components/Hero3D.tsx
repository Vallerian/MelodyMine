"use client"
import {MeshDistortMaterial, OrbitControls, Sphere} from "@react-three/drei";
import {Canvas} from "@react-three/fiber";


const Hero3D = () => {
    return (
        <Canvas
            style={{
                width: "100%",
                height: "100vh",
                overflow: "hidden"
            }}>
            <OrbitControls enableZoom={false}/>
            <ambientLight intensity={2.5}/>
            <directionalLight position={[3, 2, 1]}/>
            <Sphere args={[1, 100, 200]} scale={2.2}>
                <MeshDistortMaterial
                    color="#e20a3e"
                    attach="material"
                    distort={0.4}
                    speed={3}
                />
            </Sphere>
        </Canvas>
    )
}
export default Hero3D
